package com.scttech.cs600.module7.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.scttech.cs600.module7.model.Course;
import com.scttech.cs600.module7.model.CoursePrerequisite;
import com.scttech.cs600.module7.model.CoursePrerequisiteId;

public interface CoursePrerequisiteRepository extends JpaRepository<CoursePrerequisite, CoursePrerequisiteId> {

    /**
     * Both sides of {@link CoursePrerequisite} are lazy and Vaadin UI code runs outside a Hibernate
     * session, so the catalog grid needs them fetched up front to show prerequisite codes.
     */
    @Override
    @EntityGraph(attributePaths = { "course", "prerequisiteCourse" })
    List<CoursePrerequisite> findAll();

    @EntityGraph(attributePaths = "prerequisiteCourse")
    List<CoursePrerequisite> findByCourse(Course course);

    /**
     * Removes every row that names {@code course} on either side, so the course itself can be
     * deleted without violating the foreign keys on {@code course_prerequisites}.
     */
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("delete from CoursePrerequisite cp where cp.course = :course or cp.prerequisiteCourse = :course")
    void deleteAllInvolving(@Param("course") Course course);
}
