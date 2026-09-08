package com.scttech.cs600.module2.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.scttech.cs600.module2.model.Course;

public interface CourseRepository extends JpaRepository<Course, UUID> {

    Optional<Course> findByCourseCode(String courseCode);
}
