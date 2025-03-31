package com.droidslife.screensaver.weather

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.droidslife.screensaver.location.TimeZoneUtils
import com.droidslife.screensaver.settings.PreferencesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

/**
 * View model for weather data.
 * @param weatherRepository The weather repository.
 * @param weatherApi The weather API client.
 * @param preferencesRepository The preferences repository.
 */
class WeatherViewModel(
    private val weatherRepository: WeatherRepository,
    private val weatherApi: WeatherApi,
    private val preferencesRepository: PreferencesRepository
) {
    private val viewModelScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    /**
     * The current state of weather data.
     */
    var state by mutableStateOf<WeatherState>(WeatherState.Loading)
        private set

    /**
     * The current state of city search.
     */
    var citySearchState by mutableStateOf<CitySearchState>(CitySearchState.Initial)
        private set

    /**
     * The currently selected city.
     */
    var selectedCity by mutableStateOf<String?>(null)
        private set

    init {
        viewModelScope.launch {
            // Check if there's a stored city preference
            val storedCity = preferencesRepository.getCurrentCity()

            if (storedCity != null) {
                // If there's a stored city, load weather data for that city
                println("[DEBUG] Using stored city: $storedCity") // Debug log
                loadWeatherDataForCity(storedCity)
            } else {
                // If there's no stored city, try to get a city based on the timezone
                val timeZoneCity = TimeZoneUtils.getCityFromTimeZone(weatherApi)

                if (timeZoneCity != null) {
                    // If a city was found based on the timezone, load weather data for that city
                    // and store it as the current city
                    println("[DEBUG] Using timezone-based city: $timeZoneCity") // Debug log
                    loadWeatherDataForCity(timeZoneCity)
                    preferencesRepository.setCurrentCity(timeZoneCity)
                } else {
                    // If no city could be determined from the timezone, use a default city
                    // based on the user's region (using Mumbai as a default for India)
                    println("[DEBUG] No timezone-based city found, using default city: Mumbai") // Debug log
                    loadWeatherDataForCity("Mumbai")
                    preferencesRepository.setCurrentCity("Mumbai")
                }
            }
        }
    }

    /**
     * Loads weather data for the current location.
     */
    fun loadWeatherData() {
        weatherRepository.getWeatherData()
            .onEach { state = it }
            .launchIn(viewModelScope)
    }

    /**
     * Loads weather data for the specified city.
     * @param cityName The name of the city.
     */
    fun loadWeatherDataForCity(cityName: String) {
        viewModelScope.launch {
            state = WeatherState.Loading
            try {
                val weatherData = weatherApi.getWeatherDataByCity(cityName)
                state = WeatherState.Success(
                    weatherData = weatherData,
                    location = com.droidslife.screensaver.location.Location(
                        latitude = weatherData.location.lat,
                        longitude = weatherData.location.lon,
                        city = weatherData.location.name,
                        country = weatherData.location.country
                    )
                )
                selectedCity = cityName
            } catch (e: Exception) {
                state = WeatherState.Error(e.message ?: "Unknown error")
            }
        }
    }

    /**
     * Searches for cities matching the given query.
     * @param query The search query.
     */
    fun searchCities(query: String) {
        if (query.isBlank()) {
            citySearchState = CitySearchState.Initial
            return
        }

        viewModelScope.launch {
            citySearchState = CitySearchState.Loading
            try {
                val cities = weatherApi.searchCity(query)
                citySearchState = if (cities.isEmpty()) {
                    CitySearchState.Empty
                } else {
                    CitySearchState.Success(cities)
                }
            } catch (e: Exception) {
                citySearchState = CitySearchState.Error(e.message ?: "Unknown error")
            }
        }
    }

    /**
     * Selects a city and loads its weather data.
     * @param city The city to select.
     */
    fun selectCity(city: CitySearchResult) {
        loadWeatherDataForCity(city.name)
        citySearchState = CitySearchState.Initial

        // Store the selected city in preferences
        viewModelScope.launch {
            preferencesRepository.setCurrentCity(city.name)
        }
    }

    /**
     * Gets the weather icon URL for the current weather.
     * @return The URL of the weather icon, or null if the weather data is not available.
     */
    fun getWeatherIconUrl(): String? {
        return when (val currentState = state) {
            is WeatherState.Success -> {
                val icon = currentState.weatherData.current.condition.icon
                // The icon URL from the API starts with // which is protocol-relative
                // We need to add https: to make it a valid URL
                if (icon.startsWith("//")) "https:$icon" else icon
            }
            else -> null
        }
    }
}

/**
 * Sealed class representing the state of city search.
 */
sealed class CitySearchState {
    /**
     * Initial state (no search performed yet).
     */
    object Initial : CitySearchState()

    /**
     * Loading state.
     */
    object Loading : CitySearchState()

    /**
     * Success state with search results.
     * @param cities The list of cities found.
     */
    data class Success(val cities: List<CitySearchResult>) : CitySearchState()

    /**
     * Empty state (no cities found).
     */
    object Empty : CitySearchState()

    /**
     * Error state.
     * @param message The error message.
     */
    data class Error(val message: String) : CitySearchState()
}
