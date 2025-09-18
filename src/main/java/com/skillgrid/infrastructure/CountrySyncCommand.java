package com.skillgrid.infrastructure;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * Spring Boot component for one-time country synchronization.
 * Uses existing Spring dependencies (Jackson, JdbcTemplate).
 */
@Component
public class CountrySyncCommand {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String API_URL = "https://restcountries.com/v3.1/all";

    /**
     * Execute the country synchronization.
     * Call this method to populate countries table.
     */
    public void synchronizeCountries() {
        System.out.println("=== Country Data Synchronization ===");
        System.out.println("Fetching data from: " + API_URL);

        try {
            // Clear existing data
            clearExistingData();

            // Fetch and insert countries
            List<CountryData> countries = fetchCountriesFromApi();
            insertCountriesIntoDatabase(countries);

            System.out.println("✅ Synchronization completed successfully!");
            System.out.println("📊 " + countries.size() + " countries synchronized.");

        } catch (Exception e) {
            System.err.println("❌ Synchronization failed!");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void clearExistingData() {
        System.out.println("Clearing existing country data...");

        // Clear tables in correct order (respecting foreign keys)
        String[] tables = {
            "country_languages",
            "country_currencies",
            "country_phone_prefixes",
            "countries"
        };

        for (String table : tables) {
            int deleted = jdbcTemplate.update("DELETE FROM " + table);
            System.out.println("Cleared " + deleted + " records from " + table);
        }
    }

    private List<CountryData> fetchCountriesFromApi() throws Exception {
        System.out.println("Fetching countries from API...");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .header("Accept", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("API returned HTTP " + response.statusCode());
        }

        JsonNode root = objectMapper.readTree(response.body());
        List<CountryData> countries = new ArrayList<>();

        for (JsonNode node : root) {
            CountryData country = new CountryData();
            country.code = getTextValue(node, "cca2");
            country.name = getTextValue(node, "name", "common");
            country.nativeName = getTextValue(node, "name", "nativeName",
                    getFirstLanguageKey(node), "common");
            country.region = getTextValue(node, "region");
            country.subregion = getTextValue(node, "subregion");
            country.capital = parseCapital(node);
            country.population = node.get("population").asLong(0);
            country.area = node.get("area").asDouble(0.0);
            country.phonePrefixes = parsePhonePrefixes(node);
            country.currencies = parseCurrencies(node);
            country.languages = parseLanguages(node);

            countries.add(country);
        }

        System.out.println("Fetched " + countries.size() + " countries from API");
        return countries;
    }

    private void insertCountriesIntoDatabase(List<CountryData> countries) {
        System.out.println("Inserting countries into database...");

        String countrySql = """
            INSERT INTO countries (code, name, native_name, region, subregion, capital, population, area)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """;

        String phoneSql = """
            INSERT INTO country_phone_prefixes (country_code, phone_prefix)
            VALUES (?, ?)
            """;

        String currencySql = """
            INSERT INTO country_currencies (country_code, currency_code)
            VALUES (?, ?)
            """;

        String languageSql = """
            INSERT INTO country_languages (country_code, language_code)
            VALUES (?, ?)
            """;

        int countryCount = 0;
        int phoneCount = 0;
        int currencyCount = 0;
        int languageCount = 0;

        for (CountryData country : countries) {
            // Insert main country
            jdbcTemplate.update(countrySql,
                country.code, country.name, country.nativeName,
                country.region, country.subregion, country.capital,
                country.population, country.area);
            countryCount++;

            // Insert phone prefixes
            for (String prefix : country.phonePrefixes) {
                jdbcTemplate.update(phoneSql, country.code, prefix);
                phoneCount++;
            }

            // Insert currencies
            for (String currency : country.currencies) {
                jdbcTemplate.update(currencySql, country.code, currency);
                currencyCount++;
            }

            // Insert languages
            for (String language : country.languages) {
                jdbcTemplate.update(languageSql, country.code, language);
                languageCount++;
            }
        }

        System.out.println("Inserted: " + countryCount + " countries, " +
                         phoneCount + " phone prefixes, " +
                         currencyCount + " currencies, " +
                         languageCount + " languages");
    }

    // Helper methods for JSON parsing
    private String getTextValue(JsonNode node, String... path) {
        JsonNode current = node;
        for (String field : path) {
            if (current == null) return null;
            current = current.get(field);
        }
        return current != null ? current.asText(null) : null;
    }

    private String getFirstLanguageKey(JsonNode node) {
        JsonNode languages = node.get("languages");
        if (languages != null && languages.isObject()) {
            return languages.fieldNames().next();
        }
        return "";
    }

    private List<String> parsePhonePrefixes(JsonNode node) {
        List<String> prefixes = new ArrayList<>();
        JsonNode idd = node.get("idd");
        if (idd != null) {
            String root = getTextValue(idd, "root");
            JsonNode suffixes = idd.get("suffixes");
            if (suffixes != null && suffixes.isArray()) {
                for (JsonNode suffix : suffixes) {
                    String s = suffix.asText();
                    if (root != null && !root.isEmpty() && s != null && !s.isEmpty()) {
                        prefixes.add(root + s);
                    }
                }
            }
        }
        return prefixes;
    }

    private List<String> parseCurrencies(JsonNode node) {
        List<String> currencies = new ArrayList<>();
        JsonNode currenciesNode = node.get("currencies");
        if (currenciesNode != null && currenciesNode.isObject()) {
            currenciesNode.fieldNames().forEachRemaining(currencies::add);
        }
        return currencies;
    }

    private String parseCapital(JsonNode node) {
        JsonNode capital = node.get("capital");
        if (capital != null && capital.isArray() && capital.size() > 0) {
            return capital.get(0).asText(null);
        }
        return null;
    }

    private List<String> parseLanguages(JsonNode node) {
        List<String> languages = new ArrayList<>();
        JsonNode languagesNode = node.get("languages");
        if (languagesNode != null && languagesNode.isObject()) {
            languagesNode.fieldNames().forEachRemaining(languages::add);
        }
        return languages;
    }

    private static class CountryData {
        String code;
        String name;
        String nativeName;
        String region;
        String subregion;
        String capital;
        long population;
        double area;
        List<String> phonePrefixes = new ArrayList<>();
        List<String> currencies = new ArrayList<>();
        List<String> languages = new ArrayList<>();
    }
}