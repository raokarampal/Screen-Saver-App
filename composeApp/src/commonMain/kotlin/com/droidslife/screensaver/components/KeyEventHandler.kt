package com.droidslife.screensaver.components

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.*
import androidx.compose.runtime.*

/**
 * Class to handle all keyboard shortcuts in the application
 * Uses UiAction and State patterns to decouple from direct ViewModel dependencies
 */
class KeyEventHandler(
    private val state: KeyEventState,
    private val onAction: (KeyEventAction) -> Unit
) {
    /**
     * Handle window-level key events (from main.kt)
     */
    @OptIn(ExperimentalComposeUiApi::class)
    fun handleWindowKeyEvent(event: KeyEvent): Boolean {
        println(event.key)
        println(event.isCtrlPressed)
        if (event.type == KeyEventType.KeyDown && event.isCtrlPressed) {
            when (event.key) {
                Key.N -> {
                    // Cycle through clock designs
                    onAction(KeyEventAction.CycleClockDesign)
                    onAction(KeyEventAction.ShowToast("Ctrl + N"))
                    return true
                }
                Key.P -> {
                    // Toggle auto-changing of clock design
                    onAction(KeyEventAction.ToggleAutoChange)
                    onAction(KeyEventAction.ShowToast("Ctrl + P"))
                    return true
                }
                Key.R -> {
                    // Toggle shuffle mode for clock design
                    onAction(KeyEventAction.ToggleShuffle)
                    onAction(KeyEventAction.ShowToast("Ctrl + R"))
                    return true
                }
                Key.S -> {
                    // Show city selection dialog
                    onAction(KeyEventAction.ShowCityDialog)
                    onAction(KeyEventAction.ShowToast("Ctrl + S"))
                    return true
                }
                Key.X -> {
                    // Exit the application if exit functionality is enabled
                    onAction(KeyEventAction.ShowToast("Ctrl + X"))
                    if (state.exitOnMouseMovementEnabled) {
                        onAction(KeyEventAction.ExitApplication)
                    } else {
                        println("Exit functionality is disabled. Use Ctrl+Z to enable.")
                    }
                    return true
                }
                Key.Z -> {
                    // Toggle exit on mouse movement functionality
                    onAction(KeyEventAction.ToggleExitOnMouseMovement)
                    onAction(KeyEventAction.ShowToast("Ctrl + Z"))
                    return true
                }
                Key.C -> {
                    // Open settings dialog
                    onAction(KeyEventAction.OpenSettings)
                    onAction(KeyEventAction.ShowToast("Ctrl + C"))
                    return true
                }
                Key.H -> {
                    // Show help dialog
                    onAction(KeyEventAction.ShowHelp)
                    onAction(KeyEventAction.ShowToast("Ctrl + H"))
                    return true
                }
                Key.T -> {
                    // Toggle theme
                    onAction(KeyEventAction.ToggleTheme)
                    onAction(KeyEventAction.ShowToast("Ctrl + T"))
                    return true
                }
                else -> return false
            }
        }
        return false
    }

}

/**
 * Remember a KeyEventHandler instance
 */
@Composable
fun rememberKeyEventHandler(
    state: KeyEventState,
    onAction: (KeyEventAction) -> Unit
): KeyEventHandler {
    return remember(state, onAction) {
        KeyEventHandler(state, onAction)
    }
}
