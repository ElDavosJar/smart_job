package com.skillgrid.members.domain.model;

import com.skillgrid.commons.ids.MemberId;
import com.skillgrid.commons.utils.Email;
import com.skillgrid.commons.utils.Phone;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Domain entity representing a Member.
 * Contains individual identity information outside the platform.
 */
public class Member {

    private final MemberId id;
    private String firstName;
    private String lastName;
    private ContactInfo contactInfo;
    private Address address;
    private BirthDate birthDate;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Member(MemberId id, String firstName, String lastName, ContactInfo contactInfo,
                  Address address, BirthDate birthDate, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = Objects.requireNonNull(id, "MemberId cannot be null");
        this.firstName = Objects.requireNonNull(firstName, "First name cannot be null");
        this.lastName = Objects.requireNonNull(lastName, "Last name cannot be null");
        this.contactInfo = Objects.requireNonNull(contactInfo, "ContactInfo cannot be null");
        // Optional fields
        this.address = address;
        this.birthDate = birthDate;
        this.createdAt = Objects.requireNonNull(createdAt, "CreatedAt cannot be null");
        this.updatedAt = Objects.requireNonNull(updatedAt, "UpdatedAt cannot be null");
    }

    public static Member create(MemberId id, String firstName, String lastName, ContactInfo contactInfo) {
        LocalDateTime now = LocalDateTime.now();
        return new Member(id, firstName, lastName, contactInfo, null, null, now, now);
    }

    public static Member rehydrate(MemberId id, String firstName, String lastName, ContactInfo contactInfo,
                                  Address address, BirthDate birthDate, LocalDateTime createdAt, LocalDateTime updatedAt) {
        return new Member(id, firstName, lastName, contactInfo, address, birthDate, createdAt, updatedAt);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private MemberId id;
        private String firstName;
        private String lastName;
        private ContactInfo contactInfo;
        private Address address;
        private BirthDate birthDate;

        public Builder id(MemberId id) {
            this.id = id;
            return this;
        }

        public Builder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public Builder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public Builder contactInfo(ContactInfo contactInfo) {
            this.contactInfo = contactInfo;
            return this;
        }


        public Builder address(Address address) {
            this.address = address;
            return this;
        }

        public Builder birthDate(BirthDate birthDate) {
            this.birthDate = birthDate;
            return this;
        }

        public Member build() {
            return Member.create(id, firstName, lastName, contactInfo);
        }
    }

    public MemberId getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public Member changeName(String firstName, String lastName) {
        Objects.requireNonNull(firstName, "First name cannot be null");
        Objects.requireNonNull(lastName, "Last name cannot be null");

        return new Member(
            this.id,
            firstName,
            lastName,
            this.contactInfo,
            this.address,
            this.birthDate,
            this.createdAt,
            LocalDateTime.now()
        );
    }

    public String getLastName() {
        return lastName;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public ContactInfo getContactInfo() {
        return contactInfo;
    }

    public Member changeContactInfo(ContactInfo contactInfo) {
        Objects.requireNonNull(contactInfo, "ContactInfo cannot be null");

        return new Member(
            this.id,
            this.firstName,
            this.lastName,
            contactInfo,
            this.address,
            this.birthDate,
            this.createdAt,
            LocalDateTime.now()
        );
    }

    public String getEmail() {
        return contactInfo.getEmailValue();
    }

    public String getPhone() {
        return contactInfo.getPhoneValue();
    }

    public Address getAddress() {
        return address;
    }

    public Member changeAddress(Address address) {
        return new Member(
            this.id,
            this.firstName,
            this.lastName,
            this.contactInfo,
            address,
            this.birthDate,
            this.createdAt,
            LocalDateTime.now()
        );
    }

    public BirthDate getBirthDate() {
        return birthDate;
    }

    public Member changeBirthDate(BirthDate birthDate) {
        return new Member(
            this.id,
            this.firstName,
            this.lastName,
            this.contactInfo,
            this.address,
            birthDate,
            this.createdAt,
            LocalDateTime.now()
        );
    }

    public int getAge() {
        return birthDate != null ? birthDate.getAge() : 0;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Member member = (Member) o;
        return Objects.equals(id, member.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Member{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", contactInfo=" + contactInfo +
                ", address=" + address +
                ", birthDate=" + birthDate +
                ", age=" + getAge() +
                ", createdAt=" + createdAt +
                '}';
    }
}