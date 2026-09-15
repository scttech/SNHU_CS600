package com.scttech.cs600.module7.repository;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

import com.scttech.cs600.module7.model.department.Department;

public interface DepartmentRepository extends JpaRepository<Department, UUID> {
    Optional<Department> findByCode(String code);
}
