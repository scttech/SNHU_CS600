package com.scttech.cs600.module7.model;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.scttech.cs600.module7.model.department.Department;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "courses")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private UUID id;

    @NotBlank
    @Column(name = "course_code", nullable = false, unique = true)
    private String courseCode;

    @NotBlank
    @Column(nullable = false)
    private String title;

    @Min(1)
    @Column(nullable = false)
    private int credits;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;


    protected Course() {
        // required by JPA
    }

    public Course(String courseCode, String title, int credits) {
        this.courseCode = courseCode;
        this.title = title;
        this.credits = credits;
    }

    public Course(String courseCode, String title, int credits, Department department) {
        this.courseCode = courseCode;
        this.title = title;
        this.credits = credits;
        this.department = department;
    }

    public UUID getId() {
        return id;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }
}
