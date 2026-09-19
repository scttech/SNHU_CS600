package com.scttech.cs600.module7.repository.course;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.scttech.cs600.module7.model.course.Course;

public interface CourseRepository extends JpaRepository<Course, UUID> {

    /**
     * {@code Course.department} is lazy and Vaadin UI code runs outside a Hibernate session, so the
     * catalog grid needs the department fetched up front to show its code.
     */
    @Override
    @EntityGraph(attributePaths = "department")
    List<Course> findAll();

    Optional<Course> findByCourseCode(String courseCode);
}
