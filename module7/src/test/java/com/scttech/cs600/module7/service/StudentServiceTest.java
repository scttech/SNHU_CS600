package com.scttech.cs600.module7.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import com.scttech.cs600.module7.model.department.Department;
import com.scttech.cs600.module7.model.students.Student;
import com.scttech.cs600.module7.model.students.StudentStatus;
import com.scttech.cs600.module7.repository.students.StudentRepository;
import com.scttech.cs600.module7.service.students.StudentService;
import com.scttech.cs600.module7.service.students.exception.DuplicateStudentException;

import jakarta.persistence.EntityManager;

/**
 * Runs {@link StudentService} and the {@link Student} mapping against a real, throwaway Postgres
 * container. The persistence context is flushed and cleared before each service call so the
 * students passed in are detached, as they are when they come out of the Vaadin grid.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
@Import(StudentService.class)
class StudentServiceTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:16");

    @Autowired
    private StudentService studentService;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private EntityManager entityManager;

    private Department department() {
        Department department = new Department("CS", "Computer Science");
        entityManager.persist(department);
        return department;
    }

    private Student newStudent(String number, Department department) {
        return new Student(number, "Ada", "Lovelace", number + "@example.edu", LocalDate.of(2024, 8, 25),
                department);
    }

    private void detach() {
        entityManager.flush();
        entityManager.clear();
    }

    @Test
    void savesAStudentWithAMajorAndDefaultsToActive() {
        Department department = department();
        detach();

        Student saved = studentService.save(newStudent("A1", department));
        detach();

        Student found = studentRepository.findById(saved.getId()).orElseThrow();
        assertThat(found.getStatus()).isEqualTo(StudentStatus.ACTIVE);
        assertThat(found.getEnrollmentDate()).isEqualTo(LocalDate.of(2024, 8, 25));
        assertThat(found.getDepartment().getId()).isEqualTo(department.getId());
    }

    @Test
    void aStudentMayHaveNoMajorOrBirthDate() {
        Student saved = studentService.save(newStudent("A2", null));
        detach();

        Student found = studentRepository.findById(saved.getId()).orElseThrow();
        assertThat(found.getDepartment()).isNull();
        assertThat(found.getDateOfBirth()).isNull();
    }

    @Test
    void everyStatusRoundTrips() {
        for (StudentStatus status : StudentStatus.values()) {
            Student student = newStudent("S-" + status, null);
            student.setStatus(status);
            studentService.save(student);
        }
        detach();

        assertThat(studentRepository.findAll()).extracting(Student::getStatus)
                .containsExactlyInAnyOrder(StudentStatus.values());
    }

    @Test
    void statusIsStoredAsANativePostgresEnum() {
        Object type = entityManager.createNativeQuery(
                "select udt_name from information_schema.columns "
                        + "where table_name = 'students' and column_name = 'status'")
                .getSingleResult();

        assertThat(type).isEqualTo("studentstatus");
    }

    @Test
    void rejectsAStudentNumberThatBelongsToSomeoneElse() {
        studentService.save(newStudent("A3", null));
        detach();

        Student duplicate = new Student("A3", "Other", "Person", "other@example.edu",
                LocalDate.of(2024, 8, 25), null);

        assertThatThrownBy(() -> studentService.save(duplicate))
                .isInstanceOf(DuplicateStudentException.class)
                .hasMessageContaining("student number");
        assertThat(studentRepository.count()).isEqualTo(1);
    }

    @Test
    void rejectsAnEmailThatBelongsToSomeoneElse() {
        studentService.save(newStudent("A4", null));
        detach();

        Student duplicate = new Student("A5", "Other", "Person", "A4@example.edu",
                LocalDate.of(2024, 8, 25), null);

        assertThatThrownBy(() -> studentService.save(duplicate))
                .isInstanceOf(DuplicateStudentException.class)
                .hasMessageContaining("email");
    }

    @Test
    void resavingAStudentIsNotADuplicateOfThemselves() {
        Student saved = studentService.save(newStudent("A6", null));
        detach();

        Student edited = studentRepository.findAll().get(0);
        edited.setStatus(StudentStatus.GRADUATED);
        studentService.save(edited);
        detach();

        assertThat(studentRepository.findById(saved.getId()).orElseThrow().getStatus())
                .isEqualTo(StudentStatus.GRADUATED);
        assertThat(studentRepository.count()).isEqualTo(1);
    }

    @Test
    void deletesAStudent() {
        Student saved = studentService.save(newStudent("A7", null));
        detach();

        studentService.delete(studentRepository.findById(saved.getId()).orElseThrow());
        detach();

        assertThat(studentRepository.count()).isZero();
    }
}
