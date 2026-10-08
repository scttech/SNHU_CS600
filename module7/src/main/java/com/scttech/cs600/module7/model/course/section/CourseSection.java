package com.scttech.cs600.module7.model.course.section;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.scttech.cs600.module7.model.course.Course;
import com.scttech.cs600.module7.model.employees.faculty.FacultyDetails;
import com.scttech.cs600.module7.model.term.Term;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * A specific offering of a {@link Course} within a {@link Term} (e.g. "CS-600, Section 001, Fall
 * 2026, taught by Dr. X"), per docs/module4/README.md. {@code instructor} references {@link
 * FacultyDetails} rather than {@code employees} directly, so the database itself enforces that
 * only faculty can be assigned as an instructor.
 */
@Entity
@Table(name = "course_sections", uniqueConstraints = @UniqueConstraint(
        name = "course_sections_course_term_section_key",
        columnNames = { "course_id", "term_id", "section_number" }))
public class CourseSection {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "term_id", nullable = false)
    private Term term;

    @NotBlank
    @Size(max = 10)
    @Column(name = "section_number", nullable = false, length = 10)
    private String sectionNumber;

    /** {@code null} when the section hasn't been assigned an instructor yet. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instructor_id")
    private FacultyDetails instructor;

    @Min(1)
    @Column(name = "capacity", nullable = false)
    private int capacity;

    @Size(max = 100)
    @Column(name = "location", length = 100)
    private String location;

    @Size(max = 100)
    @Column(name = "schedule", length = 100)
    private String schedule;

    protected CourseSection() {
        // required by JPA
    }

    public CourseSection(Course course, Term term, String sectionNumber, int capacity) {
        this.course = course;
        this.term = term;
        this.sectionNumber = sectionNumber;
        this.capacity = capacity;
    }

    public UUID getId() {
        return id;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public Term getTerm() {
        return term;
    }

    public void setTerm(Term term) {
        this.term = term;
    }

    public String getSectionNumber() {
        return sectionNumber;
    }

    public void setSectionNumber(String sectionNumber) {
        this.sectionNumber = sectionNumber;
    }

    public FacultyDetails getInstructor() {
        return instructor;
    }

    public void setInstructor(FacultyDetails instructor) {
        this.instructor = instructor;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    /**
     * Two sections are the same when they have the same database id, so instances loaded by
     * different queries (or sessions) compare equal. A section that hasn't been saved yet has no id
     * and is only equal to itself.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        return o instanceof CourseSection other && id != null && id.equals(other.getId());
    }

    /**
     * Constant on purpose: {@code id} is null until the entity is persisted, and a hash that changed
     * on save would break hash-based collections holding an unsaved section.
     */
    @Override
    public int hashCode() {
        return CourseSection.class.hashCode();
    }
}
