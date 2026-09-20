package com.scttech.cs600.module7.model.employees;

/**
 * Employment status. Stored by name in {@code employees.status}, and Hibernate derives the
 * column's {@code CHECK} constraint from these constants, so this enum is the only place the
 * allowed values are defined.
 */
public enum EmployeeStatus {
    ACTIVE,
    INACTIVE,
    TERMINATED
}
