package com.droidslife.screensaver.weather

import com.droidslife.screensaver.config.Constants
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

/**
 * Client for the WeatherAPI.com API.
 * Documentation: https://app.swaggerhub.com/apis-docs/WeatherAPI.com/WeatherAPI/1.0.2
 */
class WeatherApi(
    private val client: HttpClient
) {

    /**
     * Fetches weather data for the given location.
     * @param latitude The latitude of the location.
     * @param longitude The longitude of the location.
     * @return The weather data for the location.
     */
    suspend fun getWeatherData(
        latitude: Double,
        longitude: Double
    ): WeatherData = withContext(Dispatchers.Default) {
        try {
            // Get API key from environment variable
            val apiKey = System.getenv("WEATHERAPI") ?: "YOUR_API_KEY_HERE" // Replace with your actual API key
            println("API KEY: $apiKey")

            // Build the URL with the location and API key
            val url = "${Constants.WeatherApi.BASE_URL}${Constants.WeatherApi.CURRENT_WEATHER_ENDPOINT}?q=$latitude,$longitude&key=$apiKey"

            // Make the request and parse the response
            val response = client.get(url).body<String>()
            println(response)

            // Configure JSON parser to ignore unknown keys
            val json = Json { ignoreUnknownKeys = true }
            json.decodeFromString<WeatherData>(response)
        } catch (e: Exception) {
            e.printStackTrace()
            // If there's an error (like missing API key or network issue), return mock data
            createMockWeatherData(latitude, longitude)
        }
    }

    /**
     * Fetches weather data for the given city name.
     * @param cityName The name of the city.
     * @return The weather data for the city.
     */
    suspend fun getWeatherDataByCity(cityName: String): WeatherData = withContext(Dispatchers.Default) {
        try {
            // Get API key from environment variable
            val apiKey = System.getenv("WEATHERAPI") ?: "YOUR_API_KEY_HERE" // Replace with your actual API key

            // Build the URL with the city name and API key
            val url = "${Constants.WeatherApi.BASE_URL}${Constants.WeatherApi.CURRENT_WEATHER_ENDPOINT}?q=$cityName&key=$apiKey"

            // Make the request and parse the response
            val response = client.get(url).body<String>()

            // Configure JSON parser to ignore unknown keys
            val json = Json { ignoreUnknownKeys = true }
            json.decodeFromString<WeatherData>(response)
        } catch (e: Exception) {
            e.printStackTrace()
            // If there's an error, return mock data
            createMockWeatherData(0.0, 0.0).copy(
                location = Location(
                    name = cityName,
                    region = "",
                    country = "",
                    lat = 0.0,
                    lon = 0.0,
                    tzId = "",
                    localtimeEpoch = System.currentTimeMillis() / 1000,
                    localtime = ""
                )
            )
        }
    }

    /**
     * Searches for cities matching the given query.
     * @param query The search query.
     * @return A list of city search results.
     */
    suspend fun searchCity(query: String): List<CitySearchResult> = withContext(Dispatchers.Default) {
        try {
            // Get API key from environment variable
            val apiKey = System.getenv("WEATHERAPI") ?: "YOUR_API_KEY_HERE" // Replace with your actual API key

            // Build the URL with the query and API key
            val url = "${Constants.WeatherApi.BASE_URL}/search.json?q=$query&key=$apiKey"

            // Make the request and parse the response
            val response = client.get(url).body<String>()

            // Configure JSON parser to ignore unknown keys
            val json = Json { ignoreUnknownKeys = true }
            val jsonArray = json.parseToJsonElement(response).jsonArray

            // Parse the JSON array into a list of CitySearchResult objects
            jsonArray.map { jsonElement ->
                val jsonObject = jsonElement.jsonObject
                CitySearchResult(
                    id = jsonObject["id"]?.jsonPrimitive?.content?.toIntOrNull() ?: 0,
                    name = jsonObject["name"]?.jsonPrimitive?.content ?: "",
                    region = jsonObject["region"]?.jsonPrimitive?.content ?: "",
                    country = jsonObject["country"]?.jsonPrimitive?.content ?: "",
                    lat = jsonObject["lat"]?.jsonPrimitive?.content?.toDoubleOrNull() ?: 0.0,
                    lon = jsonObject["lon"]?.jsonPrimitive?.content?.toDoubleOrNull() ?: 0.0
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // If there's an error, return an empty list
            emptyList()
        }
    }

    /**
     * Creates mock weather data for testing or when the API is unavailable.
     * @param latitude The latitude of the location.
     * @param longitude The longitude of the location.
     * @return Mock weather data.
     */
    private fun createMockWeatherData(latitude: Double, longitude: Double): WeatherData {
        return WeatherData(
            location = Location(
                name = "New York Mock",
                region = "New York",
                country = "United States of America",
                lat = latitude,
                lon = longitude,
                tzId = "America/New_York",
                localtimeEpoch = System.currentTimeMillis() / 1000,
                localtime = "2023-01-01 12:00"
            ),
            current = Current(
                lastUpdatedEpoch = System.currentTimeMillis() / 1000,
                lastUpdated = "2023-01-01 12:00",
                tempC = 22.0,
                tempF = 71.6,
                isDay = 1,
                condition = Condition(
                    text = "Sunny",
                    icon = "//cdn.weatherapi.com/weather/64x64/day/113.png",
                    code = 1000
                ),
                windMph = 5.6,
                windKph = 9.0,
                windDegree = 270,
                windDir = "W",
                pressureMb = 1012.0,
                pressureIn = 29.88,
                precipMm = 0.0,
                precipIn = 0.0,
                humidity = 65,
                cloud = 0,
                feelslikeC = 22.0,
                feelslikeF = 71.6,
                visKm = 10.0,
                visMiles = 6.2,
                uv = 5.0,
                gustMph = 7.2,
                gustKph = 11.5
            )
        )
    }
}

/**
 * Data class representing the weather data returned by the WeatherAPI.com API.
 */
@Serializable
data class WeatherData(
    val location: Location,
    val current: Current
)

@Serializable
data class Location(
    val name: String,
    val region: String,
    val country: String,
    val lat: Double,
    val lon: Double,
    @SerialName("tz_id") val tzId: String,
    @SerialName("localtime_epoch") val localtimeEpoch: Long,
    val localtime: String
)

@Serializable
data class Current(
    @SerialName("last_updated_epoch") val lastUpdatedEpoch: Long,
    @SerialName("last_updated") val lastUpdated: String,
    @SerialName("temp_c") val tempC: Double,
    @SerialName("temp_f") val tempF: Double,
    @SerialName("is_day") val isDay: Int,
    val condition: Condition,
    @SerialName("wind_mph") val windMph: Double,
    @SerialName("wind_kph") val windKph: Double,
    @SerialName("wind_degree") val windDegree: Int,
    @SerialName("wind_dir") val windDir: String,
    @SerialName("pressure_mb") val pressureMb: Double,
    @SerialName("pressure_in") val pressureIn: Double,
    @SerialName("precip_mm") val precipMm: Double,
    @SerialName("precip_in") val precipIn: Double,
    val humidity: Int,
    val cloud: Int,
    @SerialName("feelslike_c") val feelslikeC: Double,
    @SerialName("feelslike_f") val feelslikeF: Double,
    @SerialName("vis_km") val visKm: Double,
    @SerialName("vis_miles") val visMiles: Double,
    val uv: Double,
    @SerialName("gust_mph") val gustMph: Double,
    @SerialName("gust_kph") val gustKph: Double
)

@Serializable
data class Condition(
    val text: String,
    val icon: String,
    val code: Int
)

/**
 * Data class representing a city search result from the WeatherAPI.com API.
 */
@Serializable
data class CitySearchResult(
    val id: Int,
    val name: String,
    val region: String,
    val country: String,
    val lat: Double,
    val lon: Double
)
