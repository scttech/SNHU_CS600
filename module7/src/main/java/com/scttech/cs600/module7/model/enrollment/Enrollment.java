package com.scttech.cs600.module7.model.enrollment;

import java.time.LocalDate;
import java.util.UUID;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.scttech.cs600.module7.model.course.section.CourseSection;
import com.scttech.cs600.module7.model.students.Student;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Join table between {@link Student} and {@link CourseSection}, per docs/module4/README.md.
 * {@code UNIQUE (student_id, section_id)} means a student enrolls in a given section at most once.
 */
@Entity
@Table(name = "enrollments", uniqueConstraints = @UniqueConstraint(
        name = "enrollments_student_section_key", columnNames = { "student_id", "section_id" }))
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "section_id", nullable = false)
    private CourseSection section;

    @NotNull
    @ColumnDefault("CURRENT_DATE")
    @Column(name = "enrollment_date", nullable = false)
    private LocalDate enrollmentDate = LocalDate.now();

    // NAMED_ENUM maps this to a native Postgres ENUM type (see docs/module4 Conventions).
    @NotNull
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @ColumnDefault("'ENROLLED'")
    @Column(name = "status", nullable = false)
    private EnrollmentStatus status = EnrollmentStatus.ENROLLED;

    /** {@code null} until the section completes and a grade is recorded. */
    @Size(max = 5)
    @Column(name = "grade", length = 5)
    private String grade;

    protected Enrollment() {
        // required by JPA
    }

    public Enrollment(Student student, CourseSection section) {
        this.student = student;
        this.section = section;
    }

    public UUID getId() {
        return id;
    }

    public Student getStudent() {
        return student;
    }

    public CourseSection getSection() {
        return section;
    }

    public LocalDate getEnrollmentDate() {
        return enrollmentDate;
    }

    public void setEnrollmentDate(LocalDate enrollmentDate) {
        this.enrollmentDate = enrollmentDate;
    }

    public EnrollmentStatus getStatus() {
        return status;
    }

    public void setStatus(EnrollmentStatus status) {
        this.status = status;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    /**
     * Two enrollments are the same when they have the same database id, so instances loaded by
     * different queries (or sessions) compare equal. An enrollment that hasn't been saved yet has no
     * id and is only equal to itself.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        return o instanceof Enrollment other && id != null && id.equals(other.getId());
    }

    /**
     * Constant on purpose: {@code id} is null until the entity is persisted, and a hash that changed
     * on save would break hash-based collections holding an unsaved enrollment.
     */
    @Override
    public int hashCode() {
        return Enrollment.class.hashCode();
    }
}
