package com.scttech.cs600.module7.model;

import jakarta.persistence.CheckConstraint;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

/**
 * Self-referencing join table on {@link Course}, per docs/module4/README.md.
 * The {@code check} constraint below is what Hibernate turns into the table's
 * {@code CHECK (course_id <> prerequisite_course_id)} so a course can't be
 * declared its own prerequisite; the two {@code @MapsId} fields together form
 * the composite {@code (course_id, prerequisite_course_id)} primary key.
 */
@Entity
@Table(name = "course_prerequisites", check = @CheckConstraint(
        name = "course_prerequisites_not_self",
        constraint = "course_id <> prerequisite_course_id"))
public class CoursePrerequisite {

    @EmbeddedId
    private CoursePrerequisiteId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("courseId")
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("prerequisiteCourseId")
    @JoinColumn(name = "prerequisite_course_id", nullable = false)
    private Course prerequisiteCourse;

    protected CoursePrerequisite() {
        // required by JPA
    }

    public CoursePrerequisite(Course course, Course prerequisiteCourse) {
        this.course = course;
        this.prerequisiteCourse = prerequisiteCourse;
        this.id = new CoursePrerequisiteId(course.getId(), prerequisiteCourse.getId());
    }

    public CoursePrerequisiteId getId() {
        return id;
    }

    public Course getCourse() {
        return course;
    }

    public Course getPrerequisiteCourse() {
        return prerequisiteCourse;
    }
}
