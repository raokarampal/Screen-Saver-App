package com.droidslife.screensaver.settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

/**
 * ViewModel for managing application settings.
 * @param preferencesRepository The repository for storing and retrieving preferences.
 */
class SettingsViewModel(
    private val preferencesRepository: PreferencesRepository
) {
    private val viewModelScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    /**
     * The current settings state.
     */
    var settings by mutableStateOf(SettingsModel())
        private set

    /**
     * Whether the settings dialog is currently open.
     */
    var isSettingsDialogOpen by mutableStateOf(false)
        private set

    init {
        // Load settings from the repository
        preferencesRepository.getSettings()
            .onEach { settings = it }
            .launchIn(viewModelScope)
    }

    /**
     * Toggles the theme between light and dark.
     * @return The new theme state (true for dark, false for light).
     */
    fun toggleTheme(): Boolean {
        val newSettings = settings.copy(isDarkTheme = !settings.isDarkTheme)
        updateSettings(newSettings)
        return newSettings.isDarkTheme
    }

    /**
     * Sets the theme to the specified value.
     * @param isDark Whether to use dark theme.
     */
    fun setTheme(isDark: Boolean) {
        updateSettings(settings.copy(isDarkTheme = isDark))
    }

    /**
     * Toggles the clock format between 12-hour and 24-hour.
     * @return The new clock format state (true for 24-hour, false for 12-hour).
     */
    fun toggleClockFormat(): Boolean {
        val newSettings = settings.copy(is24HourFormat = !settings.is24HourFormat)
        updateSettings(newSettings)
        return newSettings.is24HourFormat
    }

    /**
     * Sets the clock format to the specified value.
     * @param is24Hour Whether to use 24-hour format.
     */
    fun setClockFormat(is24Hour: Boolean) {
        updateSettings(settings.copy(is24HourFormat = is24Hour))
    }

    /**
     * Sets the current city for weather display.
     * @param city The city name.
     */
    fun setCurrentCity(city: String) {
        updateSettings(settings.copy(currentCity = city))
    }

    /**
     * Toggles the auto play status.
     * @return The new auto play status.
     */
    fun toggleAutoPlay(): Boolean {
        val newSettings = settings.copy(autoPlayEnabled = !settings.autoPlayEnabled)
        updateSettings(newSettings)
        return newSettings.autoPlayEnabled
    }

    /**
     * Sets the auto play status.
     * @param enabled Whether auto play should be enabled.
     */
    fun setAutoPlay(enabled: Boolean) {
        updateSettings(settings.copy(autoPlayEnabled = enabled))
    }

    /**
     * Toggles the shuffle status.
     * @return The new shuffle status.
     */
    fun toggleShuffle(): Boolean {
        val newSettings = settings.copy(shuffleEnabled = !settings.shuffleEnabled)
        updateSettings(newSettings)
        return newSettings.shuffleEnabled
    }

    /**
     * Sets the shuffle status.
     * @param enabled Whether shuffle should be enabled.
     */
    fun setShuffle(enabled: Boolean) {
        updateSettings(settings.copy(shuffleEnabled = enabled))
    }

    /**
     * Sets the currently selected design.
     * @param designId The design ID.
     */
    fun setSelectedDesign(designId: Int) {
        updateSettings(settings.copy(selectedDesignId = designId))
    }

    /**
     * Updates the settings in the repository.
     * @param newSettings The new settings.
     */
    private fun updateSettings(newSettings: SettingsModel) {
        viewModelScope.launch {
            preferencesRepository.updateSettings(newSettings)
        }
    }

    /**
     * Opens the settings dialog.
     */
    fun openSettingsDialog() {
        isSettingsDialogOpen = true
    }

    /**
     * Closes the settings dialog.
     */
    fun closeSettingsDialog() {
        isSettingsDialogOpen = false
    }
}
