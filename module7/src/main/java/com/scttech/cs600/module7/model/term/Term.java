package com.scttech.cs600.module7.model.term;

import java.time.LocalDate;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.CheckConstraint;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * An academic term (e.g. "Fall 2026"), per docs/module4/README.md. {@link
 * com.scttech.cs600.module7.model.course.section.CourseSection} splits "the abstract course" from
 * "a specific offering of it" by tying a section to one of these.
 */
@Entity
@Table(name = "terms", check = @CheckConstraint(
        name = "terms_end_after_start",
        constraint = "end_date > start_date"))
public class Term {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private UUID id;

    @NotBlank
    @Size(max = 50)
    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @NotNull
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @NotNull
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    protected Term() {
        // required by JPA
    }

    public Term(String name, LocalDate startDate, LocalDate endDate) {
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    /**
     * Two terms are the same when they have the same database id, so instances loaded by different
     * queries (or sessions) compare equal. A term that hasn't been saved yet has no id and is only
     * equal to itself.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        return o instanceof Term other && id != null && id.equals(other.getId());
    }

    /**
     * Constant on purpose: {@code id} is null until the entity is persisted, and a hash that changed
     * on save would break hash-based collections holding an unsaved term.
     */
    @Override
    public int hashCode() {
        return Term.class.hashCode();
    }
}
