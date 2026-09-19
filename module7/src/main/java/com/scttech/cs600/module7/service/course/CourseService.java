package com.scttech.cs600.module7.service.course;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.scttech.cs600.module7.model.course.Course;
import com.scttech.cs600.module7.model.course.prerequisite.CoursePrerequisite;
import com.scttech.cs600.module7.repository.course.CourseRepository;
import com.scttech.cs600.module7.repository.course.prerequisite.CoursePrerequisiteRepository;
import com.scttech.cs600.module7.service.course.exception.PrerequisiteCycleException;

/**
 * The course operations that involve prerequisites. The two writes touch more than one table, so
 * each runs in a single transaction: a course and its prerequisite rows are saved (or removed)
 * together or not at all.
 */
@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final CoursePrerequisiteRepository coursePrerequisiteRepository;

    public CourseService(CourseRepository courseRepository,
            CoursePrerequisiteRepository coursePrerequisiteRepository) {
        this.courseRepository = courseRepository;
        this.coursePrerequisiteRepository = coursePrerequisiteRepository;
    }

    /**
     * Saves the course and makes its prerequisites exactly {@code prerequisites}: rows for courses
     * no longer in the set are removed and rows for newly added courses are created.
     *
     * @throws PrerequisiteCycleException if {@code prerequisites} would create a cycle; nothing is
     *         written in that case
     */
    @Transactional
    public Course save(Course course, Set<Course> prerequisites) {
        requireNoCycle(course, prerequisites);
        Course saved = courseRepository.save(course);
        List<CoursePrerequisite> existing = coursePrerequisiteRepository.findByCourse(saved);

        Set<Course> alreadyLinked = existing.stream()
                .map(CoursePrerequisite::getPrerequisiteCourse)
                .collect(Collectors.toSet());
        List<CoursePrerequisite> removed = existing.stream()
                .filter(link -> !prerequisites.contains(link.getPrerequisiteCourse()))
                .toList();
        List<CoursePrerequisite> added = prerequisites.stream()
                .filter(prerequisite -> !alreadyLinked.contains(prerequisite))
                .map(prerequisite -> new CoursePrerequisite(saved, prerequisite))
                .toList();

        coursePrerequisiteRepository.deleteAll(removed);
        coursePrerequisiteRepository.saveAll(added);
        return saved;
    }

    /**
     * The one place the no-cycle rule lives: {@link #save} calls it, so it holds for every caller,
     * and it can also be called on its own to validate ahead of a save.
     *
     * @throws PrerequisiteCycleException if any of {@code prerequisites} is {@code course} itself
     *         or already requires it, directly or through a chain of prerequisites
     */
    @Transactional(readOnly = true)
    public void requireNoCycle(Course course, Set<Course> prerequisites) {
        Set<Course> dependents = dependentsOf(course);
        Set<Course> offending = prerequisites.stream()
                .filter(prerequisite -> prerequisite.equals(course) || dependents.contains(prerequisite))
                .collect(Collectors.toSet());
        if (!offending.isEmpty()) {
            throw new PrerequisiteCycleException(course, offending);
        }
    }

    /**
     * Every course that requires {@code course}, directly or through a chain of prerequisites.
     * Making any of these a prerequisite of {@code course} would create a cycle, so the picker
     * offers none of them. Terminates even if the data already contains a cycle, and is empty
     * for a course that hasn't been saved yet.
     */
    @Transactional(readOnly = true)
    public Set<Course> dependentsOf(Course course) {
        if (course.getId() == null) {
            return Set.of();
        }
        Map<UUID, List<Course>> dependentsByPrerequisiteId = coursePrerequisiteRepository.findAll().stream()
                .collect(Collectors.groupingBy(link -> link.getPrerequisiteCourse().getId(),
                        Collectors.mapping(CoursePrerequisite::getCourse, Collectors.toList())));

        Map<UUID, Course> dependents = new HashMap<>();
        Deque<Course> pending = new ArrayDeque<>();
        pending.push(course);
        while (!pending.isEmpty()) {
            for (Course dependent : dependentsByPrerequisiteId.getOrDefault(pending.pop().getId(), List.of())) {
                if (dependents.putIfAbsent(dependent.getId(), dependent) == null) {
                    pending.push(dependent);
                }
            }
        }
        return new HashSet<>(dependents.values());
    }

    /**
     * Deletes the course along with every prerequisite row that names it, whether as the course
     * or as someone else's prerequisite.
     */
    @Transactional
    public void delete(Course course) {
        coursePrerequisiteRepository.deleteAllInvolving(course);
        courseRepository.delete(course);
    }
}
