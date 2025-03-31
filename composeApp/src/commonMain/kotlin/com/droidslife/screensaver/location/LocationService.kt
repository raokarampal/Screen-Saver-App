package com.droidslife.screensaver.location

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.TimeZone

/**
 * Service for getting the current location.
 * This is a mock implementation that returns a fixed location.
 * In a real-world scenario, this would be replaced with a proper location service
 * using platform-specific APIs.
 */
class LocationService {
    /**
     * Gets the current location.
     * @return The current location.
     */
    suspend fun getCurrentLocation(): Location = withContext(Dispatchers.Default) {
        // Get the current timezone to determine an appropriate default location
        val timezone = TimeZone.currentSystemDefault().id

        // If the timezone is Asia/Kolkata (India), return Mumbai
        if (timezone == "Asia/Kolkata") {
            println("[DEBUG] LocationService: Using Mumbai as default location for India timezone") // Debug log
            return@withContext Location(19.0760, 72.8777, "Mumbai", "IN")
        }

        // For other timezones, return New York as a fallback
        println("[DEBUG] LocationService: Using New York as default location for timezone: $timezone") // Debug log
        return@withContext Location(40.7128, -74.0060, "New York", "US")
    }
}

/**
 * Data class representing a location.
 * @param latitude The latitude of the location.
 * @param longitude The longitude of the location.
 * @param city The city name.
 * @param country The country code.
 */
data class Location(
    val latitude: Double,
    val longitude: Double,
    val city: String,
    val country: String
)
