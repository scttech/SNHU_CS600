package com.scttech.cs600.module7.repository.course.section;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.scttech.cs600.module7.model.course.Course;
import com.scttech.cs600.module7.model.course.section.CourseSection;
import com.scttech.cs600.module7.model.term.Term;

public interface CourseSectionRepository extends JpaRepository<CourseSection, UUID> {

    @EntityGraph(attributePaths = { "course", "term", "instructor" })
    List<CourseSection> findByCourseAndTerm(Course course, Term term);
}
