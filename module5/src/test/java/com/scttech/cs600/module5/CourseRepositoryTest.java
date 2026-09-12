package com.scttech.cs600.module5;

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

import com.scttech.cs600.module5.model.Course;
import com.scttech.cs600.module5.repository.CourseRepository;

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

    @Test
    void createsAndReadsACourse() {
        Course saved = courseRepository.save(new Course("CS-600", "Software Design and Development", 3));

        assertThat(saved.getId()).isNotNull();

        Optional<Course> found = courseRepository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getCourseCode()).isEqualTo("CS-600");
        assertThat(found.get().getTitle()).isEqualTo("Software Design and Development");
        assertThat(found.get().getCredits()).isEqualTo(3);
    }

    @Test
    void findsACourseByCourseCode() {
        courseRepository.save(new Course("CS-500", "Foundations of Computer Science", 3));

        Optional<Course> found = courseRepository.findByCourseCode("CS-500");

        assertThat(found).isPresent();
        assertThat(found.get().getTitle()).isEqualTo("Foundations of Computer Science");
    }

    @Test
    void updatesACourse() {
        Course saved = courseRepository.save(new Course("CS-610", "Placeholder Title", 3));

        saved.setTitle("Algorithms & Data Structures");
        courseRepository.save(saved);

        Course updated = courseRepository.findById(saved.getId()).orElseThrow();

        assertThat(updated.getTitle()).isEqualTo("Algorithms & Data Structures");
    }

    @Test
    void deletesACourse() {
        Course saved = courseRepository.save(new Course("CS-620", "Temporary Course", 3));

        courseRepository.deleteById(saved.getId());

        assertThat(courseRepository.findById(saved.getId())).isEmpty();
    }

    @Test
    void listsAllCourses() {
        courseRepository.save(new Course("CS-630", "Course A", 3));
        courseRepository.save(new Course("CS-640", "Course B", 3));

        List<Course> all = courseRepository.findAll();

        assertThat(all).hasSize(2);
    }
}
