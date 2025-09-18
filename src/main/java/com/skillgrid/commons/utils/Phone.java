package com.skillgrid.commons.utils;

import java.util.Objects;

/**
 * Value object representing a Phone number.
 * Simple validation for basic phone number requirements.
 */
public record Phone(String value) {

    public Phone {
        Objects.requireNonNull(value, "Phone value cannot be null");

        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException("Phone number cannot be empty");
        }

        if (trimmed.length() > 25) {
            throw new IllegalArgumentException("Phone number is too long (max 25 characters)");
        }

        // Extract digits for basic validation
        String digitsOnly = trimmed.replaceAll("\\D", "");
        if (digitsOnly.length() < 7) {
            throw new IllegalArgumentException("Phone number must contain at least 7 digits");
        }

        // Store the validated, trimmed value
        value = trimmed;
    }

    public static Phone of(String value) {
        return new Phone(value);
    }

    public String getDigitsOnly() {
        return value.replaceAll("\\D", "");
    }

    public boolean hasDigits() {
        return !getDigitsOnly().isEmpty();
    }

    @Override
    public String toString() {
        return value;
    }
}