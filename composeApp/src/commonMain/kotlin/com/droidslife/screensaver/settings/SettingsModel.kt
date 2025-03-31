package com.droidslife.screensaver.settings

import kotlinx.serialization.Serializable

/**
 * Model class for application settings.
 */
@Serializable
data class SettingsModel(
    /**
     * Whether the app should use dark theme.
     */
    val isDarkTheme: Boolean = true,

    /**
     * Whether the clock should use 24-hour format.
     * If false, 12-hour format with AM/PM will be used.
     */
    val is24HourFormat: Boolean = true,

    /**
     * The current city for weather display.
     * If null, the app will try to determine the city based on the timezone.
     */
    val currentCity: String? = null,

    /**
     * Whether auto play is enabled.
     */
    val autoPlayEnabled: Boolean = false,

    /**
     * Whether shuffle is enabled.
     */
    val shuffleEnabled: Boolean = false,

    /**
     * The ID of the currently selected design.
     */
    val selectedDesignId: Int = 0
)
