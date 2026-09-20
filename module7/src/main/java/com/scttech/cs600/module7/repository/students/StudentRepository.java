package com.scttech.cs600.module7.repository.students;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.scttech.cs600.module7.model.students.Student;

public interface StudentRepository extends JpaRepository<Student, UUID> {

    /**
     * {@code Student.department} is lazy and Vaadin UI code runs outside a Hibernate session, so
     * the student grid needs the department fetched up front to show the major's code.
     */
    @Override
    @EntityGraph(attributePaths = "department")
    List<Student> findAll();

    Optional<Student> findByStudentNumber(String studentNumber);

    Optional<Student> findByEmail(String email);
}
