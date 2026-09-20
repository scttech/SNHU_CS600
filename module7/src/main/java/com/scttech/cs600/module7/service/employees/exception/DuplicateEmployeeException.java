package com.scttech.cs600.module7.service.employees.exception;

/**
 * Thrown when a save would give two employees the same employee number or email, both of which
 * are unique. Checked up front so the caller gets a readable message instead of a raw constraint
 * violation; the database's unique constraints remain the backstop for concurrent saves.
 */
public class DuplicateEmployeeException extends RuntimeException {

    public DuplicateEmployeeException(String field, String value) {
        super("The " + field + " \"" + value + "\" is already in use");
    }
}
