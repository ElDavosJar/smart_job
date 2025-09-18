package com.skillgrid.commons.ids;

import java.util.Objects;
import java.util.UUID;

/**
 * Value object representing a JobSeeker ID.
 * Strict ID for job seeker profiles.
 */
public record JobSeekerId(UUID value) {

    public JobSeekerId {
        Objects.requireNonNull(value, "JobSeekerId value cannot be null");
    }

    public static JobSeekerId of(String value) {
        Objects.requireNonNull(value, "JobSeekerId string cannot be null");
        if (value.trim().isEmpty()) {
            throw new IllegalArgumentException("JobSeekerId string cannot be empty");
        }
        try {
            return new JobSeekerId(UUID.fromString(value));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid UUID format for JobSeekerId: " + value, e);
        }
    }

    @Override
    public String toString() {
        return value.toString();
    }
}