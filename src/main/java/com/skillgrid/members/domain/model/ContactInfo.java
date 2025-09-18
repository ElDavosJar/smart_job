package com.skillgrid.members.domain.model;

import com.skillgrid.commons.utils.Email;
import com.skillgrid.commons.utils.Phone;
import java.util.Objects;

/**
 * Value object representing Contact Information.
 * Composed of Email and Phone value objects.
 */
public record ContactInfo(
    Email email,
    Phone phone
) {

    public ContactInfo {
        Objects.requireNonNull(email, "Email cannot be null");
    }

    public static ContactInfo withEmail(Email email) {
        return new ContactInfo(email, null);
    }

    public static ContactInfo withEmailAndPhone(Email email, Phone phone) {
        return new ContactInfo(email, phone);
    }

    public boolean hasPhone() {
        return phone != null;
    }

    public String getEmailValue() {
        return email.value();
    }

    public String getPhoneValue() {
        return phone != null ? phone.value() : null;
    }

    @Override
    public String toString() {
        return "ContactInfo{" +
                "email=" + email +
                ", phone=" + phone +
                '}';
    }
}