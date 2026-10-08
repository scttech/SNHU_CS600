package com.scttech.cs600.module7.repository.course.section;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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
import com.scttech.cs600.module7.model.employees.Employee;
import com.scttech.cs600.module7.model.employees.EmployeeType;
import com.scttech.cs600.module7.model.employees.faculty.AcademicRank;
import com.scttech.cs600.module7.model.employees.faculty.FacultyDetails;
import com.scttech.cs600.module7.model.term.Term;
import com.scttech.cs600.module7.repository.course.CourseRepository;
import com.scttech.cs600.module7.repository.department.DepartmentRepository;
import com.scttech.cs600.module7.repository.term.TermRepository;

import jakarta.persistence.EntityManager;

/**
 * Runs the {@link CourseSectionRepository} against a real, throwaway Postgres container (via
 * Testcontainers) so the {@code course_sections_course_term_section_key} unique constraint and the
 * {@code instructor_id} foreign key into {@code faculty_details} from docs/module4/README.md are
 * exercised by the actual database, not just application code.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
class CourseSectionRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:16");

    @Autowired
    private CourseSectionRepository courseSectionRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private TermRepository termRepository;

    @Autowired
    private EntityManager entityManager;

    private Department department;
    private Course course;
    private Term term;

    private void setUpCatalog() {
        department = departmentRepository.saveAndFlush(new Department("CS", "Computer Science"));
        course = courseRepository.saveAndFlush(new Course("CS-600", "Software Design", 3, department));
        term = termRepository.saveAndFlush(new Term("Fall 2026", LocalDate.of(2026, 8, 24), LocalDate.of(2026, 12, 18)));
    }

    private FacultyDetails newInstructor(String employeeNumber) {
        Employee employee = new Employee(employeeNumber, "Ada", "Lovelace", employeeNumber + "@example.edu",
                department, EmployeeType.FACULTY, LocalDate.of(2020, 8, 15));
        entityManager.persist(employee);

        FacultyDetails details = new FacultyDetails(employee, AcademicRank.PROFESSOR);
        entityManager.persist(details);
        return details;
    }

    @Test
    void createsAndReadsASectionWithoutAnInstructor() {
        setUpCatalog();

        CourseSection saved = courseSectionRepository
                .saveAndFlush(new CourseSection(course, term, "001", 30));

        assertThat(saved.getId()).isNotNull();

        Optional<CourseSection> found = courseSectionRepository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getCourse().getCourseCode()).isEqualTo("CS-600");
        assertThat(found.get().getTerm().getName()).isEqualTo("Fall 2026");
        assertThat(found.get().getSectionNumber()).isEqualTo("001");
        assertThat(found.get().getCapacity()).isEqualTo(30);
        assertThat(found.get().getInstructor()).isNull();
    }

    @Test
    void assignsAFacultyInstructor() {
        setUpCatalog();
        FacultyDetails instructor = newInstructor("E1");

        CourseSection section = new CourseSection(course, term, "001", 30);
        section.setInstructor(instructor);
        CourseSection saved = courseSectionRepository.saveAndFlush(section);
        entityManager.clear();

        CourseSection found = courseSectionRepository.findById(saved.getId()).orElseThrow();

        assertThat(found.getInstructor().getEmployeeId()).isEqualTo(instructor.getEmployeeId());
    }

    @Test
    void rejectsADuplicateSectionNumberForTheSameCourseAndTerm() {
        setUpCatalog();
        courseSectionRepository.saveAndFlush(new CourseSection(course, term, "001", 30));

        assertThatThrownBy(() -> courseSectionRepository
                .saveAndFlush(new CourseSection(course, term, "001", 25)))
                .isInstanceOf(DataIntegrityViolationException.class)
                .hasMessageContaining("course_sections_course_term_section_key");
    }

    @Test
    void findsSectionsByCourseAndTerm() {
        setUpCatalog();
        courseSectionRepository.saveAndFlush(new CourseSection(course, term, "001", 30));
        courseSectionRepository.saveAndFlush(new CourseSection(course, term, "002", 30));

        List<CourseSection> found = courseSectionRepository.findByCourseAndTerm(course, term);

        assertThat(found).hasSize(2)
                .extracting(CourseSection::getSectionNumber)
                .containsExactlyInAnyOrder("001", "002");
    }

    @Test
    void sectionsAreEqualById() {
        setUpCatalog();
        CourseSection saved = courseSectionRepository.saveAndFlush(new CourseSection(course, term, "001", 30));
        CourseSection other = courseSectionRepository.saveAndFlush(new CourseSection(course, term, "002", 30));

        entityManager.clear();
        CourseSection reloaded = courseSectionRepository.findById(saved.getId()).orElseThrow();

        assertThat(reloaded)
                .isNotSameAs(saved)
                .isEqualTo(saved)
                .hasSameHashCodeAs(saved)
                .isNotEqualTo(other);
    }
}
