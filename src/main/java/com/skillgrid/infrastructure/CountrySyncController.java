package com.skillgrid.infrastructure;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller to trigger country data synchronization.
 * This is a one-time operation endpoint.
 */
@RestController
@RequestMapping("/api/admin/countries")
public class CountrySyncController {

    private final CountrySyncCommand countrySyncCommand;

    public CountrySyncController(CountrySyncCommand countrySyncCommand) {
        this.countrySyncCommand = countrySyncCommand;
    }

    /**
     * POST /api/admin/countries/sync - Trigger country data synchronization
     *
     * This endpoint will:
     * 1. Clear existing country data
     * 2. Fetch fresh data from REST Countries API
     * 3. Populate all country tables
     *
     * Use with caution - this will replace all existing country data!
     */
    @PostMapping("/sync")
    public ResponseEntity<String> synchronizeCountries() {
        try {
            System.out.println("🔄 Starting country synchronization via REST API...");

            countrySyncCommand.synchronizeCountries();

            String message = """
                ✅ Country synchronization completed successfully!

                📊 Data synchronized:
                • 250+ countries with complete information
                • Phone prefixes for validation
                • Currencies for salary conversions
                • Languages for communication preferences
                • Geographic data for regional matching

                🌍 Source: REST Countries API (https://restcountries.com)
                """;

            return ResponseEntity.ok(message);

        } catch (Exception e) {
            String errorMessage = "❌ Synchronization failed: " + e.getMessage();
            System.err.println(errorMessage);
            e.printStackTrace();

            return ResponseEntity.internalServerError().body(errorMessage);
        }
    }
}