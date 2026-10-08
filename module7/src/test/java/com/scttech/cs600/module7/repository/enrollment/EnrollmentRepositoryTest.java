package com.scttech.cs600.module7.repository.enrollment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.dao.DataIntegrityViolationException;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import com.scttech.cs600.module7.model.course.Course;
import com.scttech.cs600.module7.model.course.section.CourseSection;
import com.scttech.cs600.module7.model.department.Department;
import com.scttech.cs600.module7.model.enrollment.Enrollment;
import com.scttech.cs600.module7.model.enrollment.EnrollmentStatus;
import com.scttech.cs600.module7.model.students.Student;
import com.scttech.cs600.module7.model.term.Term;
import com.scttech.cs600.module7.repository.course.CourseRepository;
import com.scttech.cs600.module7.repository.course.section.CourseSectionRepository;
import com.scttech.cs600.module7.repository.department.DepartmentRepository;
import com.scttech.cs600.module7.repository.students.StudentRepository;
import com.scttech.cs600.module7.repository.term.TermRepository;

import jakarta.persistence.EntityManager;

/**
 * Runs the {@link EnrollmentRepository} against a real, throwaway Postgres container (via
 * Testcontainers) so the {@code enrollments_student_section_key} unique constraint and the native
 * {@code enrollmentstatus} enum from docs/module4/README.md are exercised by the actual database,
 * not just application code.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
class EnrollmentRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:16");

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private TermRepository termRepository;

    @Autowired
    private CourseSectionRepository courseSectionRepository;

    @Autowired
    private EntityManager entityManager;

    private Course course;
    private Term term;

    private Student newStudent(String studentNumber) {
        return studentRepository.saveAndFlush(new Student(studentNumber, "Grace", "Hopper",
                studentNumber + "@example.edu", LocalDate.of(2026, 8, 24), null));
    }

    /**
     * Persists a section of a shared {@code CS-600} course and {@code Fall 2026} term, creating that
     * catalog on first use so multiple sections in the same test don't collide on the department's
     * unique code.
     */
    private CourseSection newSection(String sectionNumber) {
        if (course == null) {
            Department department = departmentRepository.saveAndFlush(new Department("CS", "Computer Science"));
            course = courseRepository.saveAndFlush(new Course("CS-600", "Software Design", 3, department));
            term = termRepository
                    .saveAndFlush(new Term("Fall 2026", LocalDate.of(2026, 8, 24), LocalDate.of(2026, 12, 18)));
        }

        return courseSectionRepository.saveAndFlush(new CourseSection(course, term, sectionNumber, 30));
    }

    @Test
    void createsAndReadsAnEnrollmentWithDefaults() {
        Student student = newStudent("S1");
        CourseSection section = newSection("001");

        Enrollment saved = enrollmentRepository.saveAndFlush(new Enrollment(student, section));
        entityManager.clear();

        Enrollment found = enrollmentRepository.findById(saved.getId()).orElseThrow();

        assertThat(found.getStudent().getId()).isEqualTo(student.getId());
        assertThat(found.getSection().getId()).isEqualTo(section.getId());
        assertThat(found.getStatus()).isEqualTo(EnrollmentStatus.ENROLLED);
        assertThat(found.getEnrollmentDate()).isNotNull();
        assertThat(found.getGrade()).isNull();
    }

    @Test
    void recordsAGradeAndAStatusChange() {
        Student student = newStudent("S2");
        CourseSection section = newSection("001");
        Enrollment saved = enrollmentRepository.saveAndFlush(new Enrollment(student, section));

        saved.setStatus(EnrollmentStatus.COMPLETED);
        saved.setGrade("A");
        enrollmentRepository.saveAndFlush(saved);
        entityManager.clear();

        Enrollment updated = enrollmentRepository.findById(saved.getId()).orElseThrow();

        assertThat(updated.getStatus()).isEqualTo(EnrollmentStatus.COMPLETED);
        assertThat(updated.getGrade()).isEqualTo("A");
    }

    @Test
    void rejectsEnrollingTheSameStudentInTheSameSectionTwice() {
        Student student = newStudent("S3");
        CourseSection section = newSection("001");
        enrollmentRepository.saveAndFlush(new Enrollment(student, section));

        assertThatThrownBy(() -> enrollmentRepository.saveAndFlush(new Enrollment(student, section)))
                .isInstanceOf(DataIntegrityViolationException.class)
                .hasMessageContaining("enrollments_student_section_key");
    }

    @Test
    void findsEnrollmentsByStudent() {
        Student student = newStudent("S4");
        CourseSection sectionOne = newSection("001");
        CourseSection sectionTwo = newSection("002");
        enrollmentRepository.saveAndFlush(new Enrollment(student, sectionOne));
        enrollmentRepository.saveAndFlush(new Enrollment(student, sectionTwo));

        List<Enrollment> found = enrollmentRepository.findByStudent(student);

        assertThat(found).hasSize(2);
    }

    @Test
    void findsEnrollmentsBySection() {
        CourseSection section = newSection("001");
        Student studentOne = newStudent("S5");
        Student studentTwo = newStudent("S6");
        enrollmentRepository.saveAndFlush(new Enrollment(studentOne, section));
        enrollmentRepository.saveAndFlush(new Enrollment(studentTwo, section));

        List<Enrollment> found = enrollmentRepository.findBySection(section);

        assertThat(found).hasSize(2);
    }

    @Test
    void enrollmentsAreEqualById() {
        Student student = newStudent("S7");
        CourseSection sectionOne = newSection("001");
        CourseSection sectionTwo = newSection("002");
        Enrollment saved = enrollmentRepository.saveAndFlush(new Enrollment(student, sectionOne));
        Enrollment other = enrollmentRepository.saveAndFlush(new Enrollment(student, sectionTwo));

        entityManager.clear();
        Enrollment reloaded = enrollmentRepository.findById(saved.getId()).orElseThrow();

        assertThat(reloaded)
                .isNotSameAs(saved)
                .isEqualTo(saved)
                .hasSameHashCodeAs(saved)
                .isNotEqualTo(other);
    }

    @Test
    void unsavedEnrollmentsAreOnlyEqualToThemselves() {
        Student student = newStudent("S8");
        CourseSection section = newSection("001");
        Enrollment first = new Enrollment(student, section);
        Enrollment second = new Enrollment(student, section);

        assertThat(first).isEqualTo(first).isNotEqualTo(second);
    }
}
