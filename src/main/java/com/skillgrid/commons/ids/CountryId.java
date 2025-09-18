package com.skillgrid.commons.ids;

import java.util.Objects;
import java.util.UUID;

/**
 * Value object representing a Country ID.
 * Uses UUID for uniqueness across countries.
 */
public record CountryId(UUID value) {

    public CountryId {
        Objects.requireNonNull(value, "CountryId value cannot be null");
    }

    public static CountryId of(String value) {
        Objects.requireNonNull(value, "CountryId string cannot be null");
        if (value.trim().isEmpty()) {
            throw new IllegalArgumentException("CountryId string cannot be empty");
        }
        try {
            return new CountryId(UUID.fromString(value));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid UUID format for CountryId: " + value, e);
        }
    }

    @Override
    public String toString() {
        return value.toString();
    }
}