package com.scttech.cs600.module7.repository.course;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import com.scttech.cs600.module7.model.course.Course;
import com.scttech.cs600.module7.model.department.Department;
import com.scttech.cs600.module7.repository.course.CourseRepository;
import com.scttech.cs600.module7.repository.department.DepartmentRepository;

import jakarta.persistence.EntityManager;

/**
 * Runs the {@link CourseRepository} CRUD operations against a real, throwaway
 * Postgres container (via Testcontainers) rather than the docker-compose one used
 * by the running app, so `mvn test` works standalone. Each @Test method runs in
 * its own transaction that Spring rolls back afterward, so the table is empty at
 * the start of every test below.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
class CourseRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:16");

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void createsAndReadsACourse() {
        Course saved = courseRepository.saveAndFlush(newCourse("CS-600", "Software Design and Development", 3));

        assertThat(saved.getId()).isNotNull();

        Optional<Course> found = courseRepository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getCourseCode()).isEqualTo("CS-600");
        assertThat(found.get().getTitle()).isEqualTo("Software Design and Development");
        assertThat(found.get().getCredits()).isEqualTo(3);
    }

    @Test
    void findsACourseByCourseCode() {
        courseRepository.saveAndFlush(newCourse("CS-500", "Foundations of Computer Science", 3));

        Optional<Course> found = courseRepository.findByCourseCode("CS-500");

        assertThat(found).isPresent();
        assertThat(found.get().getTitle()).isEqualTo("Foundations of Computer Science");
    }

    @Test
    void updatesACourse() {
        Course saved = courseRepository.saveAndFlush(newCourse("CS-610", "Placeholder Title", 3));

        saved.setTitle("Algorithms & Data Structures");
        courseRepository.saveAndFlush(saved);

        Course updated = courseRepository.findById(saved.getId()).orElseThrow();

        assertThat(updated.getTitle()).isEqualTo("Algorithms & Data Structures");
    }

    @Test
    void deletesACourse() {
        Course saved = courseRepository.saveAndFlush(newCourse("CS-620", "Temporary Course", 3));

        courseRepository.deleteById(saved.getId());

        assertThat(courseRepository.findById(saved.getId())).isEmpty();
    }

    @Test
    void listsAllCourses() {
        courseRepository.saveAndFlush(newCourse("CS-630", "Course A", 3));
        courseRepository.saveAndFlush(newCourse("CS-640", "Course B", 3));

        List<Course> all = courseRepository.findAll();

        assertThat(all).hasSize(2);
    }

    @Test
    void coursesAreEqualById() {
        Course saved = courseRepository.saveAndFlush(newCourse("CS-650", "Course A", 3));
        Course other = courseRepository.saveAndFlush(newCourse("CS-660", "Course B", 3));

        entityManager.clear();
        Course reloaded = courseRepository.findById(saved.getId()).orElseThrow();

        assertThat(reloaded)
                .isNotSameAs(saved)
                .isEqualTo(saved)
                .hasSameHashCodeAs(saved)
                .isNotEqualTo(other);
    }

    @Test
    void unsavedCoursesAreOnlyEqualToThemselves() {
        Course first = new Course("CS-670", "Course C", 3);
        Course second = new Course("CS-670", "Course C", 3);

        assertThat(first).isEqualTo(first).isNotEqualTo(second);
    }

    /**
     * Builds a {@link Course} with a freshly persisted {@link Department} attached,
     * since {@code department_id} is a required foreign key. The department code is
     * derived from the course code to keep it unique per test.
     */
    private Course newCourse(String courseCode, String title, int credits) {
        Department department = departmentRepository
                .saveAndFlush(new Department(courseCode, courseCode + " Department"));

        return new Course(courseCode, title, credits, department);
    }
}
