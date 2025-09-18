-- Create countries table for storing country data
-- This table will store static country information for matching purposes

CREATE TABLE countries (
    code VARCHAR(3) PRIMARY KEY,  -- ISO 3166-1 alpha-2 or alpha-3
    name VARCHAR(100) NOT NULL,
    native_name VARCHAR(100),
    region VARCHAR(50),
    subregion VARCHAR(50),
    capital VARCHAR(100),
    population BIGINT,
    area DECIMAL(15,2),  -- Area in km²
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create table for country phone prefixes (many-to-many relationship)
CREATE TABLE country_phone_prefixes (
    country_code VARCHAR(3) NOT NULL,
    phone_prefix VARCHAR(10) NOT NULL,
    PRIMARY KEY (country_code, phone_prefix),
    FOREIGN KEY (country_code) REFERENCES countries(code) ON DELETE CASCADE
);

-- Create table for country currencies (many-to-many relationship)
CREATE TABLE country_currencies (
    country_code VARCHAR(3) NOT NULL,
    currency_code VARCHAR(3) NOT NULL,
    PRIMARY KEY (country_code, currency_code),
    FOREIGN KEY (country_code) REFERENCES countries(code) ON DELETE CASCADE
);

-- Create table for country languages (many-to-many relationship)
CREATE TABLE country_languages (
    country_code VARCHAR(3) NOT NULL,
    language_code VARCHAR(3) NOT NULL,
    PRIMARY KEY (country_code, language_code),
    FOREIGN KEY (country_code) REFERENCES countries(code) ON DELETE CASCADE
);

-- Create indexes for better query performance
CREATE INDEX idx_countries_region ON countries(region);
CREATE INDEX idx_countries_subregion ON countries(subregion);
CREATE INDEX idx_country_phone_prefixes_prefix ON country_phone_prefixes(phone_prefix);
CREATE INDEX idx_country_currencies_code ON country_currencies(currency_code);

-- Add comments for documentation
COMMENT ON TABLE countries IS 'Static table containing country information for job matching';
COMMENT ON TABLE country_phone_prefixes IS 'Phone prefixes associated with countries';
COMMENT ON TABLE country_currencies IS 'Currencies used by countries';
COMMENT ON TABLE country_languages IS 'Languages spoken in countries';