package com.scttech.cs600.module7.controller;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.scttech.cs600.module7.model.Course;
import com.scttech.cs600.module7.repository.CourseRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * Same endpoints as module 2's controller. What's new here is HTTP Basic auth, enforced by
 * {@link com.scttech.cs600.module7.config.ApiSecurityConfig} — every operation below
 * requires it.
 */
@RestController
@RequestMapping("/api/courses")
@Tag(name = "Courses", description = "Operations on the course catalog")
@SecurityRequirement(name = "basicAuth")
public class CourseController {

    private final CourseRepository courseRepository;

    public CourseController(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    @GetMapping
    @Operation(summary = "List all courses")
    public List<Course> findAll() {
        return courseRepository.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a course by id")
    public Course findById(@PathVariable UUID id) {
        return courseRepository.findById(id).orElseThrow(() -> notFound(id));
    }

    @PostMapping
    @Operation(summary = "Create a course")
    public ResponseEntity<Course> create(@Valid @RequestBody Course course) {
        Course saved = courseRepository.save(course);
        return ResponseEntity.created(URI.create("/api/courses/" + saved.getId())).body(saved);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing course")
    public Course update(@PathVariable UUID id, @Valid @RequestBody Course update) {
        Course existing = courseRepository.findById(id).orElseThrow(() -> notFound(id));
        existing.setCourseCode(update.getCourseCode());
        existing.setTitle(update.getTitle());
        existing.setCredits(update.getCredits());
        return courseRepository.save(existing);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a course")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        if (!courseRepository.existsById(id)) {
            throw notFound(id);
        }
        courseRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<String> handleDuplicateCourseCode() {
        return ResponseEntity.status(HttpStatus.CONFLICT).body("A course with that course code already exists");
    }

    private ResponseStatusException notFound(UUID id) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Course " + id + " not found");
    }
}
