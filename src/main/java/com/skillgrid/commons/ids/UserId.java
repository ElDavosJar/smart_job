package com.skillgrid.commons.ids;

import java.util.Objects;
import java.util.UUID;

/**
 * Value object representing a User ID.
 * Uses UUID for uniqueness across the system.
 */
public record UserId(UUID value) {

    public UserId {
        Objects.requireNonNull(value, "UserId value cannot be null");
    }

    public static UserId of(String value) {
        Objects.requireNonNull(value, "UserId string cannot be null");
        if (value.trim().isEmpty()) {
            throw new IllegalArgumentException("UserId string cannot be empty");
        }
        try {
            return new UserId(UUID.fromString(value));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid UUID format for UserId: " + value, e);
        }
    }

    @Override
    public String toString() {
        return value.toString();
    }
}