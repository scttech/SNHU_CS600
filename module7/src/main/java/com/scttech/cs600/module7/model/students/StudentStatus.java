package com.scttech.cs600.module7.model.students;

/**
 * Where a student is in their enrollment. Stored by name in {@code students.status} as a native
 * Postgres {@code ENUM}, so this enum is the only place the allowed values are defined.
 */
public enum StudentStatus {
    ACTIVE,
    INACTIVE,
    GRADUATED,
    WITHDRAWN
}
