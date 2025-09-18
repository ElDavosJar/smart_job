package com.skillgrid.commons.ids;

import java.util.Objects;
import java.util.UUID;

/**
 * Value object representing an Employer ID.
 * Strict ID for employer profiles.
 */
public record EmployerId(UUID value) {

    public EmployerId {
        Objects.requireNonNull(value, "EmployerId value cannot be null");
    }

    public static EmployerId of(String value) {
        Objects.requireNonNull(value, "EmployerId string cannot be null");
        if (value.trim().isEmpty()) {
            throw new IllegalArgumentException("EmployerId string cannot be empty");
        }
        try {
            return new EmployerId(UUID.fromString(value));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid UUID format for EmployerId: " + value, e);
        }
    }

    @Override
    public String toString() {
        return value.toString();
    }
}