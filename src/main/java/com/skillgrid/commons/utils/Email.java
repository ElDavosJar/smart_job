package com.skillgrid.commons.utils;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Value object representing an Email address.
 * Comprehensive validation following RFC 5322 and modern email standards.
 */
public record Email(String value) {

    // RFC 5322 compliant pattern with additional security considerations
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^(?=.{1,254}$)" +  // Total length 1-254 chars
        "(?=.{1,64}@)" +   // Local part max 64 chars
        "[A-Za-z0-9!#$%&'*+/=?^_`{|}~-]+" +  // Local part allowed chars
        "(?:\\.[A-Za-z0-9!#$%&'*+/=?^_`{|}~-]+)*" +  // Local part with dots
        "@" +
        "(?=.{1,253}$)" +  // Domain part 1-253 chars
        "(?=.{1,63}\\.)" + // Domain label max 63 chars before dot
        "(?:[A-Za-z0-9](?:[A-Za-z0-9-]*[A-Za-z0-9])?\\.)*" +  // Domain labels
        "[A-Za-z0-9](?:[A-Za-z0-9-]*[A-Za-z0-9])?$",  // Final domain label
        Pattern.CASE_INSENSITIVE
    );


    public Email {
        Objects.requireNonNull(value, "Email value cannot be null");

        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException("Email address cannot be empty or whitespace only");
        }

        if (trimmed.length() > 254) {
            throw new IllegalArgumentException("Email address exceeds maximum length of 254 characters");
        }

        // Check for basic @ presence
        if (!trimmed.contains("@")) {
            throw new IllegalArgumentException("Email address must contain '@' symbol");
        }

        // Check for multiple @ symbols
        if (trimmed.chars().filter(ch -> ch == '@').count() > 1) {
            throw new IllegalArgumentException("Email address cannot contain multiple '@' symbols");
        }

        // Validate against RFC 5322 pattern
        if (!EMAIL_PATTERN.matcher(trimmed).matches()) {
            throw new IllegalArgumentException("Invalid email format. Email must follow RFC 5322 standards: " + value);
        }


        // Check for consecutive dots
        if (trimmed.contains("..")) {
            throw new IllegalArgumentException("Email address cannot contain consecutive dots: " + value);
        }

        // Check for dots at start or end of local/domain parts
        String localPart = trimmed.substring(0, trimmed.indexOf('@'));
        String domainPart = trimmed.substring(trimmed.indexOf('@') + 1);

        if (localPart.startsWith(".") || localPart.endsWith(".")) {
            throw new IllegalArgumentException("Local part cannot start or end with a dot: " + value);
        }

        if (domainPart.startsWith(".") || domainPart.endsWith(".")) {
            throw new IllegalArgumentException("Domain part cannot start or end with a dot: " + value);
        }

        // Check domain has at least one dot
        if (!domainPart.contains(".")) {
            throw new IllegalArgumentException("Domain must contain at least one dot: " + value);
        }

        // Check for valid TLD (at least 2 characters)
        String tld = domainPart.substring(domainPart.lastIndexOf('.') + 1);
        if (tld.length() < 2) {
            throw new IllegalArgumentException("Top-level domain must be at least 2 characters: " + value);
        }

        // Store the validated, trimmed value
        value = trimmed;
    }

    public static Email of(String value) {
        return new Email(value);
    }

    public String getLocalPart() {
        return value.substring(0, value.indexOf('@'));
    }

    public String getDomainPart() {
        return value.substring(value.indexOf('@') + 1);
    }

    public String getTopLevelDomain() {
        String domain = getDomainPart();
        return domain.substring(domain.lastIndexOf('.') + 1);
    }


    public boolean hasSubdomain() {
        String domain = getDomainPart();
        return domain.chars().filter(ch -> ch == '.').count() > 1;
    }

    @Override
    public String toString() {
        return value;
    }
}