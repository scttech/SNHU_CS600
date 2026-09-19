package com.scttech.cs600.module7.repository.course.prerequisite;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
import com.scttech.cs600.module7.model.course.prerequisite.CoursePrerequisite;
import com.scttech.cs600.module7.model.department.Department;
import com.scttech.cs600.module7.repository.course.CourseRepository;
import com.scttech.cs600.module7.repository.department.DepartmentRepository;

/**
 * Runs the {@link CoursePrerequisiteRepository} against a real, throwaway
 * Postgres container (via Testcontainers) so the {@code course_prerequisites_not_self}
 * CHECK constraint from docs/module4/README.md is exercised by the actual database,
 * not just application code.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
class CoursePrerequisiteRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:16");

    @Autowired
    private CoursePrerequisiteRepository coursePrerequisiteRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Test
    void savesAPrerequisiteBetweenTwoDistinctCourses() {
        Course cs300 = newCourse("CS-300");
        Course cs600 = newCourse("CS-600");

        coursePrerequisiteRepository.saveAndFlush(new CoursePrerequisite(cs600, cs300));

        List<CoursePrerequisite> all = coursePrerequisiteRepository.findAll();

        assertThat(all).hasSize(1);
        assertThat(all.get(0).getCourse().getCourseCode()).isEqualTo("CS-600");
        assertThat(all.get(0).getPrerequisiteCourse().getCourseCode()).isEqualTo("CS-300");
    }

    @Test
    void rejectsACourseAsItsOwnPrerequisite() {
        Course cs600 = newCourse("CS-600");

        assertThatThrownBy(() -> coursePrerequisiteRepository
                .saveAndFlush(new CoursePrerequisite(cs600, cs600)))
                .isInstanceOf(DataIntegrityViolationException.class)
                .hasMessageContaining("course_prerequisites_not_self");
    }

    /**
     * Builds and persists a {@link Course} with a freshly persisted {@link Department}
     * attached, since {@code department_id} is a required foreign key. The department
     * code is derived from the course code to keep it unique per test.
     */
    private Course newCourse(String courseCode) {
        Department department = departmentRepository
                .saveAndFlush(new Department(courseCode, courseCode + " Department"));

        return courseRepository.saveAndFlush(new Course(courseCode, courseCode + " Title", 3, department));
    }
}
