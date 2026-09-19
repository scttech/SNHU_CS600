package com.scttech.cs600.module7.model.department;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "departments")
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private UUID id;

    @NotBlank 
    @Size(max = 10)
    @Column(name = "code", nullable = false, unique = true)
    private String code;

    @NotBlank 
    @Size(max = 150)
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    protected Department() {
        // required by JPA
    }

    public Department(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public Department(String code, String name, String description) {
        this.code = code;
        this.name = name;
        this.description = description;
    }

    public UUID getId() {
        return id;
    }

    /**
     * Return the department code
     * @return Unique code for the department
     */
    public String getCode() {
        return code;
    }

    /**
     * Set the department code
     * @param code Unique code for the department
     */
    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Two departments are the same when they have the same database id, so instances loaded by
     * different queries (or sessions) compare equal. A department that hasn't been saved yet has no
     * id and is only equal to itself.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        return o instanceof Department other && id != null && id.equals(other.getId());
    }

    /**
     * Constant on purpose: {@code id} is null until the entity is persisted, and a hash that
     * changed on save would break hash-based collections holding an unsaved department.
     */
    @Override
    public int hashCode() {
        return Department.class.hashCode();
    }

}
