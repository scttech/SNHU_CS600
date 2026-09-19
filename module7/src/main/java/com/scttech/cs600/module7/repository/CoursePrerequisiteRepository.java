package com.scttech.cs600.module7.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.scttech.cs600.module7.model.CoursePrerequisite;
import com.scttech.cs600.module7.model.CoursePrerequisiteId;

public interface CoursePrerequisiteRepository extends JpaRepository<CoursePrerequisite, CoursePrerequisiteId> {
}
