package com.skillgrid.members.domain.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Value object representing a Birth Date.
 * Validates that the person is at least 16 years old and born after 1900.
 */
public record BirthDate(LocalDateTime value) {

    private static final LocalDate MIN_DATE = LocalDate.of(1900, 1, 1);
    private static final int MINIMUM_AGE = 16;

    public BirthDate {
        Objects.requireNonNull(value, "Birth date cannot be null");

        LocalDate birthDate = value.toLocalDate();
        LocalDate today = LocalDate.now();

        // Check minimum date (1900)
        if (birthDate.isBefore(MIN_DATE)) {
            throw new IllegalArgumentException("Birth date cannot be before 1900");
        }

        // Check maximum date (must be at least 16 years old)
        LocalDate minimumBirthDate = today.minusYears(MINIMUM_AGE);
        if (birthDate.isAfter(minimumBirthDate)) {
            throw new IllegalArgumentException("Person must be at least " + MINIMUM_AGE + " years old");
        }

        // Check not in future
        if (birthDate.isAfter(today)) {
            throw new IllegalArgumentException("Birth date cannot be in the future");
        }
    }

    public static BirthDate of(LocalDateTime dateTime) {
        return new BirthDate(dateTime);
    }

    public static BirthDate of(LocalDate date) {
        return new BirthDate(date.atStartOfDay());
    }

    public int getAge() {
        return LocalDate.now().getYear() - value.getYear();
    }

    public LocalDate toLocalDate() {
        return value.toLocalDate();
    }

    public String format(String pattern) {
        return value.format(DateTimeFormatter.ofPattern(pattern));
    }

    public String format() {
        return value.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    @Override
    public String toString() {
        return format();
    }
}