package com.droidslife.screensaver.components

/**
 * Sealed class representing all possible UI actions that can be triggered by key events.
 * This decouples the KeyEventHandler from direct ViewModel dependencies.
 */
sealed class KeyEventAction {
    /**
     * Action to cycle through clock designs.
     */
    object CycleClockDesign : KeyEventAction()

    /**
     * Action to toggle auto-changing of clock design.
     */
    object ToggleAutoChange : KeyEventAction()

    /**
     * Action to toggle shuffle mode for clock design.
     */
    object ToggleShuffle : KeyEventAction()

    /**
     * Action to show city selection dialog.
     */
    object ShowCityDialog : KeyEventAction()

    /**
     * Action to exit the application.
     */
    object ExitApplication : KeyEventAction()

    /**
     * Action to toggle exit on mouse movement functionality.
     */
    object ToggleExitOnMouseMovement : KeyEventAction()

    /**
     * Action to open settings dialog.
     */
    object OpenSettings : KeyEventAction()

    /**
     * Action to show help dialog.
     */
    object ShowHelp : KeyEventAction()

    /**
     * Action to toggle theme.
     */
    object ToggleTheme : KeyEventAction()

    /**
     * Action to show a toast notification.
     * @param message The message to show in the toast.
     */
    data class ShowToast(val message: String) : KeyEventAction()
}

/**
 * Data class representing the current UI state that the KeyEventHandler needs to know about.
 * This decouples the KeyEventHandler from direct ViewModel dependencies.
 */
data class KeyEventState(
    /**
     * Whether exit on mouse movement is enabled.
     */
    val exitOnMouseMovementEnabled: Boolean = true
)