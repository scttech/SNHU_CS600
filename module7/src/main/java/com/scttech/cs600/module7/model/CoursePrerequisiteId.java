package com.scttech.cs600.module7.model;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.Embeddable;

/**
 * Composite key for {@link CoursePrerequisite}, mirroring the composite
 * {@code (course_id, prerequisite_course_id)} primary key from
 * docs/module4/README.md.
 */
@Embeddable
public class CoursePrerequisiteId implements Serializable {

    private UUID courseId;
    private UUID prerequisiteCourseId;

    protected CoursePrerequisiteId() {
        // required by JPA
    }

    public CoursePrerequisiteId(UUID courseId, UUID prerequisiteCourseId) {
        this.courseId = courseId;
        this.prerequisiteCourseId = prerequisiteCourseId;
    }

    public UUID getCourseId() {
        return courseId;
    }

    public UUID getPrerequisiteCourseId() {
        return prerequisiteCourseId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CoursePrerequisiteId that)) {
            return false;
        }
        return Objects.equals(courseId, that.courseId)
                && Objects.equals(prerequisiteCourseId, that.prerequisiteCourseId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(courseId, prerequisiteCourseId);
    }
}
