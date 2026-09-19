package com.scttech.cs600.module7.service.course.exception;

import java.util.Set;
import java.util.stream.Collectors;

import com.scttech.cs600.module7.model.course.Course;
import com.scttech.cs600.module7.service.course.CourseService;

/**
 * Thrown by {@link CourseService} when a set of prerequisites would make a course impossible to
 * satisfy: it names the course itself, or a course that already requires it (directly or through
 * a chain). Unchecked so it rolls back the surrounding transaction; each caller decides how to
 * report it (the UI shows a notification, a REST endpoint would map it to a 409).
 */
public class PrerequisiteCycleException extends RuntimeException {

    private final transient Set<Course> offending;

    public PrerequisiteCycleException(Course course, Set<Course> offending) {
        super(course.getCourseCode() + " can't require "
                + offending.stream().map(Course::getCourseCode).sorted().collect(Collectors.joining(", "))
                + ": that would create a prerequisite cycle");
        this.offending = Set.copyOf(offending);
    }

    /** The requested prerequisites that would have caused the cycle. */
    public Set<Course> getOffending() {
        return offending;
    }
}
