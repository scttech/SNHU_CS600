package com.scttech.cs600.module7.model.employees.faculty;

/**
 * Faculty tenure status. Stored by name in {@code faculty_details.tenure_status} as a native
 * Postgres {@code ENUM}, so this enum is the only place the allowed values are defined.
 */
public enum TenureStatus {
    TENURED,
    TENURE_TRACK,
    NON_TENURE
}
