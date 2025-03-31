package com.droidslife.screensaver.weather

import com.droidslife.screensaver.location.LocationService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Repository for weather data.
 * @param weatherApi The weather API client.
 * @param locationService The location service.
 */
class WeatherRepository(
    private val weatherApi: WeatherApi,
    private val locationService: LocationService
) {
    /**
     * Gets the weather data for the current location.
     * @return A flow of weather data.
     */
    fun getWeatherData(): Flow<WeatherState> = flow {
        emit(WeatherState.Loading)
        try {
            val location = locationService.getCurrentLocation()
            val weatherData = weatherApi.getWeatherData(location.latitude, location.longitude)
            emit(WeatherState.Success(weatherData, location))
        } catch (e: Exception) {
            emit(WeatherState.Error(e.message ?: "Unknown error"))
        }
    }
}

/**
 * Sealed class representing the state of weather data.
 */
sealed class WeatherState {
    /**
     * Loading state.
     */
    object Loading : WeatherState()

    /**
     * Success state.
     * @param weatherData The weather data.
     * @param location The location.
     */
    data class Success(
        val weatherData: WeatherData,
        val location: com.droidslife.screensaver.location.Location
    ) : WeatherState()

    /**
     * Error state.
     * @param message The error message.
     */
    data class Error(val message: String) : WeatherState()
}
