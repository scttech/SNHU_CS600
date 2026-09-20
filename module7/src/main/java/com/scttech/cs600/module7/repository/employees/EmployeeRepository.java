package com.scttech.cs600.module7.repository.employees;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.scttech.cs600.module7.model.employees.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, UUID> {

    /**
     * {@code Employee.department} is lazy and Vaadin UI code runs outside a Hibernate session, so
     * the employee grid needs the department fetched up front to show its code.
     */
    @Override
    @EntityGraph(attributePaths = "department")
    List<Employee> findAll();

    Optional<Employee> findByEmployeeNumber(String employeeNumber);

    Optional<Employee> findByEmail(String email);
}
