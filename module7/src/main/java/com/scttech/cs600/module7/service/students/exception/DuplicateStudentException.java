package com.scttech.cs600.module7.service.students.exception;

/**
 * Thrown when a save would give two students the same student number or email, both of which are
 * unique. Checked up front so the caller gets a readable message instead of a raw constraint
 * violation; the database's unique constraints remain the backstop for concurrent saves.
 */
public class DuplicateStudentException extends RuntimeException {

    public DuplicateStudentException(String field, String value) {
        super("The " + field + " \"" + value + "\" is already in use");
    }
}
