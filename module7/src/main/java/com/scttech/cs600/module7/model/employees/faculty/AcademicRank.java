package com.scttech.cs600.module7.model.employees.faculty;

/**
 * Faculty academic rank. Stored by name in {@code faculty_details.academic_rank} as a native
 * Postgres {@code ENUM}, so this enum is the only place the allowed values are defined.
 */
public enum AcademicRank {
    INSTRUCTOR,
    ASSISTANT_PROFESSOR,
    ASSOCIATE_PROFESSOR,
    PROFESSOR,
    ADJUNCT
}
