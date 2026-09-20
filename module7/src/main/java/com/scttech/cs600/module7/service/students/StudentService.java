package com.scttech.cs600.module7.service.students;

import org.springframework.stereotype.Service;

import com.scttech.cs600.module7.model.students.Student;
import com.scttech.cs600.module7.repository.students.StudentRepository;
import com.scttech.cs600.module7.service.students.exception.DuplicateStudentException;

/**
 * The student operations. A student is a single row, so nothing here needs a multi-table
 * transaction; the service exists so the unique student number and email are checked in one place
 * and reported readably, whoever is saving.
 */
@Service
public class StudentService {

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    /**
     * Saves the student.
     *
     * @throws DuplicateStudentException if the student number or email belongs to someone else
     */
    public Student save(Student student) {
        requireUnique(student);
        return studentRepository.saveAndFlush(student);
    }

    public void delete(Student student) {
        studentRepository.delete(student);
    }

    private void requireUnique(Student student) {
        studentRepository.findByStudentNumber(student.getStudentNumber())
                .filter(other -> !other.equals(student))
                .ifPresent(other -> {
                    throw new DuplicateStudentException("student number", student.getStudentNumber());
                });
        studentRepository.findByEmail(student.getEmail())
                .filter(other -> !other.equals(student))
                .ifPresent(other -> {
                    throw new DuplicateStudentException("email", student.getEmail());
                });
    }
}
