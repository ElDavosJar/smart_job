package com.skillgrid.members.domain.model;

import com.skillgrid.commons.ids.CountryId;
import com.skillgrid.commons.ids.CityId;
import java.util.Objects;

/**
 * Value object representing an Address.
 * Composed of CountryId and CityId for proper domain relationships.
 */
public record Address(
    String line1,
    String line2,
    CityId cityId,
    String state,
    CountryId countryId,
    String postalCode
) {

    public Address {
        Objects.requireNonNull(line1, "Address line1 cannot be null");
        Objects.requireNonNull(cityId, "CityId cannot be null");
        Objects.requireNonNull(countryId, "CountryId cannot be null");
    }

    public String getFullAddress() {
        StringBuilder address = new StringBuilder(line1);
        if (line2 != null && !line2.trim().isEmpty()) {
            address.append(", ").append(line2);
        }
        if (state != null && !state.trim().isEmpty()) {
            address.append(", ").append(state);
        }
        if (postalCode != null && !postalCode.trim().isEmpty()) {
            address.append(" ").append(postalCode);
        }
        return address.toString();
    }

    @Override
    public String toString() {
        return getFullAddress();
    }
}