package com.scttech.cs600.module7.repository.enrollment;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.scttech.cs600.module7.model.course.section.CourseSection;
import com.scttech.cs600.module7.model.enrollment.Enrollment;
import com.scttech.cs600.module7.model.students.Student;

public interface EnrollmentRepository extends JpaRepository<Enrollment, UUID> {

    @EntityGraph(attributePaths = "section")
    List<Enrollment> findByStudent(Student student);

    @EntityGraph(attributePaths = "student")
    List<Enrollment> findBySection(CourseSection section);
}
