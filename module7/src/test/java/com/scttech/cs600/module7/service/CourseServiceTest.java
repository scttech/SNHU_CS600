package com.scttech.cs600.module7.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import com.scttech.cs600.module7.model.Course;
import com.scttech.cs600.module7.model.CoursePrerequisite;
import com.scttech.cs600.module7.model.department.Department;
import com.scttech.cs600.module7.repository.CoursePrerequisiteRepository;
import com.scttech.cs600.module7.repository.CourseRepository;
import com.scttech.cs600.module7.repository.DepartmentRepository;

import jakarta.persistence.EntityManager;

/**
 * Runs {@link CourseService} against a real, throwaway Postgres container. The persistence context
 * is flushed and cleared before each service call so the courses passed in are detached, as they
 * are when they come out of the Vaadin grid and prerequisite picker.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
@Import(CourseService.class)
class CourseServiceTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:16");

    @Autowired
    private CourseService courseService;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CoursePrerequisiteRepository coursePrerequisiteRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void savesANewCourseWithSeveralPrerequisites() {
        Course cs300 = persistedCourse("CS-300");
        Course cs350 = persistedCourse("CS-350");
        Course cs600 = newCourse("CS-600");
        detach();

        Course saved = courseService.save(cs600, Set.of(cs300, cs350));

        assertThat(saved.getId()).isNotNull();
        assertThat(prerequisiteCodesOf(saved)).containsExactlyInAnyOrder("CS-300", "CS-350");
    }

    @Test
    void addsAndRemovesPrerequisitesOfAnExistingCourse() {
        Course cs300 = persistedCourse("CS-300");
        Course cs350 = persistedCourse("CS-350");
        Course cs400 = persistedCourse("CS-400");
        Course cs600 = persistedCourse("CS-600");
        detach();
        courseService.save(cs600, Set.of(cs300, cs350));
        detach();

        courseService.save(cs600, Set.of(cs350, cs400));

        assertThat(prerequisiteCodesOf(cs600)).containsExactlyInAnyOrder("CS-350", "CS-400");
    }

    @Test
    void clearsPrerequisitesWhenNoneAreSelected() {
        Course cs300 = persistedCourse("CS-300");
        Course cs600 = persistedCourse("CS-600");
        detach();
        courseService.save(cs600, Set.of(cs300));
        detach();

        courseService.save(cs600, Set.of());

        assertThat(prerequisiteCodesOf(cs600)).isEmpty();
    }

    @Test
    void deletesACourseAlongWithRowsNamingItOnEitherSide() {
        Course cs300 = persistedCourse("CS-300");
        Course cs600 = persistedCourse("CS-600");
        Course cs700 = persistedCourse("CS-700");
        detach();
        courseService.save(cs600, Set.of(cs300));
        courseService.save(cs700, Set.of(cs600));
        detach();

        courseService.delete(cs600);
        detach();

        assertThat(coursePrerequisiteRepository.count()).isZero();
        assertThat(courseRepository.findAll()).extracting(Course::getCourseCode)
                .containsExactlyInAnyOrder("CS-300", "CS-700");
    }

    @Test
    void findsEveryCourseThatRequiresACourseDirectlyOrThroughAChain() {
        Course cs100 = persistedCourse("CS-100");
        Course cs200 = persistedCourse("CS-200");
        Course cs300 = persistedCourse("CS-300");
        Course cs400 = persistedCourse("CS-400");
        Course cs900 = persistedCourse("CS-900");
        detach();
        courseService.save(cs200, Set.of(cs100));
        courseService.save(cs300, Set.of(cs200));
        courseService.save(cs400, Set.of(cs200, cs900));
        detach();

        assertThat(codes(courseService.dependentsOf(cs100))).containsExactlyInAnyOrder("CS-200", "CS-300", "CS-400");
        assertThat(codes(courseService.dependentsOf(cs200))).containsExactlyInAnyOrder("CS-300", "CS-400");
        assertThat(courseService.dependentsOf(cs300)).isEmpty();
    }

    @Test
    void dependentsOfACourseWithNoDependentsIsEmpty() {
        Course cs100 = persistedCourse("CS-100");
        Course cs200 = persistedCourse("CS-200");
        detach();

        assertThat(courseService.dependentsOf(cs100)).isEmpty();
        assertThat(courseService.dependentsOf(cs200)).isEmpty();
    }

    @Test
    void findingDependentsTerminatesWhenTheDataAlreadyContainsACycle() {
        Course cs100 = persistedCourse("CS-100");
        Course cs200 = persistedCourse("CS-200");
        // Written directly: CourseService.save would refuse to create this cycle.
        coursePrerequisiteRepository.save(new CoursePrerequisite(cs100, cs200));
        coursePrerequisiteRepository.save(new CoursePrerequisite(cs200, cs100));
        detach();

        assertThat(codes(courseService.dependentsOf(cs100))).containsExactlyInAnyOrder("CS-100", "CS-200");
    }

    @Test
    void rejectsACourseAsItsOwnPrerequisite() {
        Course cs600 = persistedCourse("CS-600");
        detach();

        assertThatThrownBy(() -> courseService.save(cs600, Set.of(cs600)))
                .isInstanceOf(PrerequisiteCycleException.class)
                .hasMessageContaining("CS-600 can't require CS-600");
    }

    @Test
    void rejectsAPrerequisiteThatDirectlyRequiresTheCourse() {
        Course cs100 = persistedCourse("CS-100");
        Course cs200 = persistedCourse("CS-200");
        detach();
        courseService.save(cs200, Set.of(cs100));
        detach();

        assertThatThrownBy(() -> courseService.save(cs100, Set.of(cs200)))
                .isInstanceOf(PrerequisiteCycleException.class)
                .hasMessageContaining("CS-100 can't require CS-200");
    }

    @Test
    void rejectsAPrerequisiteThatRequiresTheCourseThroughAChain() {
        Course cs100 = persistedCourse("CS-100");
        Course cs200 = persistedCourse("CS-200");
        Course cs300 = persistedCourse("CS-300");
        Course cs400 = persistedCourse("CS-400");
        detach();
        courseService.save(cs200, Set.of(cs100));
        courseService.save(cs300, Set.of(cs200));
        detach();

        assertThatThrownBy(() -> courseService.save(cs100, Set.of(cs300, cs400)))
                .isInstanceOfSatisfying(PrerequisiteCycleException.class, e -> {
                    // Only the offending course is named; CS-400 is a fine prerequisite.
                    assertThat(codes(e.getOffending())).containsExactly("CS-300");
                    assertThat(e.getMessage()).contains("CS-100 can't require CS-300");
                });
    }

    @Test
    void writesNothingWhenACycleIsRejected() {
        Course cs100 = persistedCourse("CS-100");
        Course cs200 = persistedCourse("CS-200");
        detach();
        courseService.save(cs200, Set.of(cs100));
        detach();
        cs100.setTitle("Changed title");

        assertThatThrownBy(() -> courseService.save(cs100, Set.of(cs200)))
                .isInstanceOf(PrerequisiteCycleException.class);
        detach();

        assertThat(courseRepository.findById(cs100.getId()).orElseThrow().getTitle()).isEqualTo("CS-100 Title");
        assertThat(prerequisiteCodesOf(cs100)).isEmpty();
        assertThat(prerequisiteCodesOf(cs200)).containsExactly("CS-100");
    }

    @Test
    void allowsPrerequisitesThatShareAnAncestorWithoutFormingACycle() {
        Course cs100 = persistedCourse("CS-100");
        Course cs200 = persistedCourse("CS-200");
        Course cs250 = persistedCourse("CS-250");
        Course cs300 = persistedCourse("CS-300");
        detach();
        courseService.save(cs200, Set.of(cs100));
        courseService.save(cs250, Set.of(cs100));
        detach();

        courseService.save(cs300, Set.of(cs200, cs250));

        assertThat(prerequisiteCodesOf(cs300)).containsExactlyInAnyOrder("CS-200", "CS-250");
    }

    @Test
    void requireNoCycleCanBeCalledOnItsOwn() {
        Course cs100 = persistedCourse("CS-100");
        Course cs200 = persistedCourse("CS-200");
        detach();
        courseService.save(cs200, Set.of(cs100));
        detach();

        courseService.requireNoCycle(cs200, Set.of(cs100));
        assertThatThrownBy(() -> courseService.requireNoCycle(cs100, Set.of(cs200)))
                .isInstanceOf(PrerequisiteCycleException.class);
    }

    private List<String> codes(Set<Course> courses) {
        return courses.stream().map(Course::getCourseCode).toList();
    }

    private List<String> prerequisiteCodesOf(Course course) {
        detach();
        return coursePrerequisiteRepository.findByCourse(course).stream()
                .map(CoursePrerequisite::getPrerequisiteCourse)
                .map(Course::getCourseCode)
                .toList();
    }

    private void detach() {
        entityManager.flush();
        entityManager.clear();
    }

    private Course persistedCourse(String courseCode) {
        return courseRepository.saveAndFlush(newCourse(courseCode));
    }

    /** Department code is derived from the course code to keep it unique per test. */
    private Course newCourse(String courseCode) {
        Department department = departmentRepository
                .saveAndFlush(new Department(courseCode, courseCode + " Department"));

        return new Course(courseCode, courseCode + " Title", 3, department);
    }
}
