package com.skillgrid.commons.ids;

import java.util.Objects;
import java.util.UUID;

/**
 * Value object representing a Member ID.
 * Uses UUID for uniqueness across members.
 */
public record MemberId(UUID value) {

    public MemberId {
        Objects.requireNonNull(value, "MemberId value cannot be null");
    }

    public static MemberId of(String value) {
        Objects.requireNonNull(value, "MemberId string cannot be null");
        if (value.trim().isEmpty()) {
            throw new IllegalArgumentException("MemberId string cannot be empty");
        }
        try {
            return new MemberId(UUID.fromString(value));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid UUID format for MemberId: " + value, e);
        }
    }

    @Override
    public String toString() {
        return value.toString();
    }
}