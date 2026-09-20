package com.scttech.cs600.module7.repository.employees.faculty;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.scttech.cs600.module7.model.employees.faculty.FacultyDetails;

/** Keyed by the employee's id, since {@link FacultyDetails} shares its employee's primary key. */
public interface FacultyDetailsRepository extends JpaRepository<FacultyDetails, UUID> {
}
