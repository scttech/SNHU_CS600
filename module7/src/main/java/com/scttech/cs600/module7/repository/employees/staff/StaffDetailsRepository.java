package com.scttech.cs600.module7.repository.employees.staff;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.scttech.cs600.module7.model.employees.staff.StaffDetails;

/** Keyed by the employee's id, since {@link StaffDetails} shares its employee's primary key. */
public interface StaffDetailsRepository extends JpaRepository<StaffDetails, UUID> {
}
