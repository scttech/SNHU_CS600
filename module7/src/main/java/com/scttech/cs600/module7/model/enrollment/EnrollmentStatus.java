package com.scttech.cs600.module7.model.enrollment;

/**
 * Where an enrollment is in its lifecycle. Stored by name in {@code enrollments.status} as a
 * native Postgres {@code ENUM}, so this enum is the only place the allowed values are defined.
 */
public enum EnrollmentStatus {
    ENROLLED,
    DROPPED,
    COMPLETED,
    WITHDRAWN
}
