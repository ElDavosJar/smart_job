package com.skillgrid.commons.ids;

import java.util.Objects;
import java.util.UUID;

/**
 * Value object representing a City ID.
 * Uses UUID for uniqueness across cities.
 */
public record CityId(UUID value) {

    public CityId {
        Objects.requireNonNull(value, "CityId value cannot be null");
    }

    public static CityId of(String value) {
        Objects.requireNonNull(value, "CityId string cannot be null");
        if (value.trim().isEmpty()) {
            throw new IllegalArgumentException("CityId string cannot be empty");
        }
        try {
            return new CityId(UUID.fromString(value));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid UUID format for CityId: " + value, e);
        }
    }

    @Override
    public String toString() {
        return value.toString();
    }
}