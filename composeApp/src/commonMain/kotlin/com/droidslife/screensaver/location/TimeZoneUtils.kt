package com.droidslife.screensaver.location

import com.droidslife.screensaver.weather.WeatherApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone

/**
 * Utility functions for working with time zones.
 */
object TimeZoneUtils {
    /**
     * Common time zone to city mappings.
     * This is a fallback for when the API call fails.
     */
    // Major cities in India (all share the same time zone: Asia/Kolkata)
    private val indianCities = listOf(
        "New Delhi", "Mumbai", "Kolkata", "Chennai", "Bangalore", "Hyderabad", 
        "Pune", "Ahmedabad", "Jaipur", "Lucknow"
    )

    private val timeZoneToCityMap = mapOf(
        // North America
        "America/New_York" to "New York",
        "America/Los_Angeles" to "Los Angeles",
        "America/Chicago" to "Chicago",
        "America/Denver" to "Denver",
        "America/Phoenix" to "Phoenix",
        "America/Toronto" to "Toronto",
        "America/Vancouver" to "Vancouver",
        "America/Mexico_City" to "Mexico City",

        // South America
        "America/Sao_Paulo" to "Sao Paulo",
        "America/Buenos_Aires" to "Buenos Aires",
        "America/Lima" to "Lima",
        "America/Bogota" to "Bogota",
        "America/Santiago" to "Santiago",

        // Europe
        "Europe/London" to "London",
        "Europe/Paris" to "Paris",
        "Europe/Berlin" to "Berlin",
        "Europe/Rome" to "Rome",
        "Europe/Madrid" to "Madrid",
        "Europe/Amsterdam" to "Amsterdam",
        "Europe/Zurich" to "Zurich",
        "Europe/Moscow" to "Moscow",
        "Europe/Istanbul" to "Istanbul",
        "Europe/Athens" to "Athens",
        "Europe/Vienna" to "Vienna",
        "Europe/Stockholm" to "Stockholm",
        "Europe/Oslo" to "Oslo",

        // Asia - India is handled separately with indianCities
        "Asia/Kolkata" to "New Delhi", // Default for India

        // Asia - Other
        "Asia/Tokyo" to "Tokyo",
        "Asia/Shanghai" to "Shanghai",
        "Asia/Hong_Kong" to "Hong Kong",
        "Asia/Singapore" to "Singapore",
        "Asia/Dubai" to "Dubai",
        "Asia/Seoul" to "Seoul",
        "Asia/Bangkok" to "Bangkok",
        "Asia/Jakarta" to "Jakarta",
        "Asia/Manila" to "Manila",
        "Asia/Kuala_Lumpur" to "Kuala Lumpur",
        "Asia/Taipei" to "Taipei",
        "Asia/Riyadh" to "Riyadh",
        "Asia/Tel_Aviv" to "Tel Aviv",

        // Africa
        "Africa/Cairo" to "Cairo",
        "Africa/Lagos" to "Lagos",
        "Africa/Johannesburg" to "Johannesburg",
        "Africa/Nairobi" to "Nairobi",
        "Africa/Casablanca" to "Casablanca",

        // Australia and Oceania
        "Australia/Sydney" to "Sydney",
        "Australia/Melbourne" to "Melbourne",
        "Australia/Perth" to "Perth",
        "Australia/Brisbane" to "Brisbane",
        "Pacific/Auckland" to "Auckland",
        "Pacific/Honolulu" to "Honolulu"
    )

    /**
     * Gets the current system time zone.
     * @return The current time zone.
     */
    fun getCurrentTimeZone(): TimeZone {
        return TimeZone.currentSystemDefault()
    }

    /**
     * Gets a city based on the time zone.
     * Uses the predefined mapping to determine the city based on the timezone.
     * @param weatherApi The Weather API client (not used directly, but kept for API compatibility).
     * @param timeZone The time zone to get a city for. If null, uses the current system time zone.
     * @return The city name, or null if no city could be determined.
     */
    suspend fun getCityFromTimeZone(weatherApi: WeatherApi, timeZone: TimeZone? = null): String? = withContext(Dispatchers.Default) {
        val tz = timeZone ?: getCurrentTimeZone()
        val tzId = tz.id

        println("[DEBUG] Current timezone: $tzId") // Debug log to see the actual timezone

        // Special case for India (Asia/Kolkata time zone)
        if (tzId == "Asia/Kolkata") {
            // Randomly select one of the Indian cities
            val city = indianCities.random()
            println("[DEBUG] Selected Indian city: $city") // Debug log
            return@withContext city
        }

        // For other time zones, try to get a city from the predefined mapping
        val mappedCity = timeZoneToCityMap[tzId]
        if (mappedCity != null) {
            println("[DEBUG] Found mapped city: $mappedCity for timezone: $tzId") // Debug log
            return@withContext mappedCity
        }

        // If no mapping is found, return null
        println("[DEBUG] No mapping found for timezone: $tzId") // Debug log
        return@withContext null
    }
}
