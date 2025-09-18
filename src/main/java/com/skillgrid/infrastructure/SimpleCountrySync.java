package com.skillgrid.infrastructure;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Simple, pure Java synchronization for countries.
 * No Spring Boot, just JDBC and HTTP client.
 */
public class SimpleCountrySync {

    private static final String API_URL = "https://restcountries.com/v3.1/all";
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/skillgrid_db";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "123456";

    public static void main(String[] args) {
        System.out.println("=== Simple Country Data Synchronization ===");
        System.out.println("Fetching data from: " + API_URL);
        System.out.println("Database: " + DB_URL);

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

    private static void clearExistingData() throws Exception {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            // Disable foreign key checks temporarily
            try (PreparedStatement stmt = conn.prepareStatement("SET CONSTRAINTS ALL DEFERRED")) {
                stmt.execute();
            }

            // Clear tables in correct order
            String[] tables = {
                "country_languages",
                "country_currencies",
                "country_phone_prefixes",
                "countries"
            };

            for (String table : tables) {
                try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM " + table)) {
                    int deleted = stmt.executeUpdate();
                    System.out.println("Cleared " + deleted + " records from " + table);
                }
            }
        }
    }

    private static List<CountryData> fetchCountriesFromApi() throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .header("Accept", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("API returned HTTP " + response.statusCode());
        }

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(response.body());

        List<CountryData> countries = new ArrayList<>();
        for (JsonNode node : root) {
            CountryData country = new CountryData();
            country.id = UUID.randomUUID().toString();
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

    private static void insertCountriesIntoDatabase(List<CountryData> countries) throws Exception {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            conn.setAutoCommit(false);

            // Insert countries
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
                try (PreparedStatement stmt = conn.prepareStatement(countrySql)) {
                    stmt.setString(1, country.code);
                    stmt.setString(2, country.name);
                    stmt.setString(3, country.nativeName);
                    stmt.setString(4, country.region);
                    stmt.setString(5, country.subregion);
                    stmt.setString(6, country.capital);
                    stmt.setLong(7, country.population);
                    stmt.setDouble(8, country.area);
                    stmt.executeUpdate();
                    countryCount++;
                }

                // Insert phone prefixes
                for (String prefix : country.phonePrefixes) {
                    try (PreparedStatement stmt = conn.prepareStatement(phoneSql)) {
                        stmt.setString(1, country.code);
                        stmt.setString(2, prefix);
                        stmt.executeUpdate();
                        phoneCount++;
                    }
                }

                // Insert currencies
                for (String currency : country.currencies) {
                    try (PreparedStatement stmt = conn.prepareStatement(currencySql)) {
                        stmt.setString(1, country.code);
                        stmt.setString(2, currency);
                        stmt.executeUpdate();
                        currencyCount++;
                    }
                }

                // Insert languages
                for (String language : country.languages) {
                    try (PreparedStatement stmt = conn.prepareStatement(languageSql)) {
                        stmt.setString(1, country.code);
                        stmt.setString(2, language);
                        stmt.executeUpdate();
                        languageCount++;
                    }
                }
            }

            conn.commit();
            System.out.println("Inserted: " + countryCount + " countries, " +
                             phoneCount + " phone prefixes, " +
                             currencyCount + " currencies, " +
                             languageCount + " languages");
        }
    }

    // Helper methods
    private static String getTextValue(JsonNode node, String... path) {
        JsonNode current = node;
        for (String field : path) {
            if (current == null) return null;
            current = current.get(field);
        }
        return current != null ? current.asText(null) : null;
    }

    private static String getFirstLanguageKey(JsonNode node) {
        JsonNode languages = node.get("languages");
        if (languages != null && languages.isObject()) {
            return languages.fieldNames().next();
        }
        return "";
    }

    private static List<String> parsePhonePrefixes(JsonNode node) {
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

    private static List<String> parseCurrencies(JsonNode node) {
        List<String> currencies = new ArrayList<>();
        JsonNode currenciesNode = node.get("currencies");
        if (currenciesNode != null && currenciesNode.isObject()) {
            currenciesNode.fieldNames().forEachRemaining(currencies::add);
        }
        return currencies;
    }

    private static String parseCapital(JsonNode node) {
        JsonNode capital = node.get("capital");
        if (capital != null && capital.isArray() && capital.size() > 0) {
            return capital.get(0).asText(null);
        }
        return null;
    }

    private static List<String> parseLanguages(JsonNode node) {
        List<String> languages = new ArrayList<>();
        JsonNode languagesNode = node.get("languages");
        if (languagesNode != null && languagesNode.isObject()) {
            languagesNode.fieldNames().forEachRemaining(languages::add);
        }
        return languages;
    }

    private static class CountryData {
        String id;
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