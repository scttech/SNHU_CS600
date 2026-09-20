package com.scttech.cs600.module7.model.employees;

/**
 * Kind of employee. Stored by name in {@code employees.employee_type}, and Hibernate derives the
 * column's {@code CHECK} constraint from these constants, so this enum is the only place the
 * allowed values are defined.
 */
public enum EmployeeType {
    FACULTY,
    STAFF
}
