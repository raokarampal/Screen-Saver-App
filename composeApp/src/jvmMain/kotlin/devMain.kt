import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.*
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.window.*
import com.droidslife.screensaver.App
import com.droidslife.screensaver.clock.ClockViewModel
import com.droidslife.screensaver.components.ShortcutToast
import com.droidslife.screensaver.components.rememberToastState
import com.droidslife.screensaver.di.appModule
import com.droidslife.screensaver.di.initKoin
import com.droidslife.screensaver.settings.SettingsViewModel
import org.jetbrains.compose.reload.DevelopmentEntryPoint
import org.koin.core.context.GlobalContext

@OptIn(ExperimentalComposeUiApi::class)
fun main() = application {
    initKoin {
        modules(appModule)
    }

    // State for showing city selection dialog
    var showCitySelectionDialog by mutableStateOf(false)

    // State for tracking whether exit on mouse movement is enabled
    var exitOnMouseMovementEnabled by remember { mutableStateOf(true) }

    // State for showing help dialog
    var showHelpDialog by remember { mutableStateOf(false) }

    // Create toast state for showing shortcut notifications
    val toastState = rememberToastState()

    Window(
        title = "Screen Saver App",
        state = rememberWindowState(
            placement = WindowPlacement.Maximized,
            position = WindowPosition(Alignment.Center),
        ),
        onCloseRequest = ::exitApplication,
        resizable = false,
        alwaysOnTop = true,
        undecorated = true,
        onKeyEvent = { event ->
            if (event.type == KeyEventType.KeyDown && event.isCtrlPressed) {
                when (event.key) {
                    Key.N -> {
                        // Get the ClockViewModel from the Koin container
                        val clockViewModel = GlobalContext.get().get<ClockViewModel>()

                        // Cycle through clock designs (1-5)
                        clockViewModel.cycleClockDesign()
                        println("Clock design changed to: ${clockViewModel.clockDesign}")
                        toastState.show("Ctrl + N")
                        true
                    }
                    Key.P -> {
                        // Toggle auto-changing of clock design
                        val clockViewModel = GlobalContext.get().get<ClockViewModel>()
                        val isEnabled = clockViewModel.toggleAutoChange()
                        println("Auto-change ${if (isEnabled) "enabled" else "disabled"}")
                        toastState.show("Ctrl + P")
                        true
                    }
                    Key.R -> {
                        // Toggle shuffle mode for clock design
                        val clockViewModel = GlobalContext.get().get<ClockViewModel>()
                        val isEnabled = clockViewModel.toggleShuffleMode()
                        println("Shuffle mode ${if (isEnabled) "enabled" else "disabled"}")
                        toastState.show("Ctrl + R")
                        true
                    }
                    Key.S -> {
                        // Show city selection dialog
                        showCitySelectionDialog = true
                        println("City selection dialog shown")
                        toastState.show("Ctrl + S")
                        true
                    }
                    Key.X -> {
                        // Exit the application if exit functionality is enabled
                        if (exitOnMouseMovementEnabled) {
                            println("Exiting application via Ctrl+X shortcut")
                            toastState.show("Ctrl + X")
                            exitApplication()
                        } else {
                            println("Exit functionality is disabled. Use Ctrl+Z to enable.")
                        }
                        true
                    }
                    Key.Z -> {
                        // Toggle exit on mouse movement functionality
                        exitOnMouseMovementEnabled = !exitOnMouseMovementEnabled
                        println("Exit on mouse movement ${if (exitOnMouseMovementEnabled) "enabled" else "disabled"}")
                        toastState.show("Ctrl + Z")
                        true
                    }
                    Key.C -> {
                        // Open settings dialog
                        val settingsViewModel = GlobalContext.get().get<SettingsViewModel>()
                        settingsViewModel.openSettingsDialog()
                        println("Settings dialog opened")
                        toastState.show("Ctrl + C")
                        true
                    }
                    Key.H -> {
                        // Show help dialog
                        showHelpDialog = true
                        println("Help dialog shown")
                        toastState.show("Ctrl + H")
                        true
                    }
                    Key.T -> {
                        // Toggle theme
                        val settingsViewModel = GlobalContext.get().get<SettingsViewModel>()
                        settingsViewModel.toggleTheme()
                        println("Theme toggled")
                        toastState.show("Ctrl + T")
                        true
                    }
                    else -> false
                }
            } else {
                false
            }
        }
    ) {
        DevelopmentEntryPoint {
                // Create a modifier with mouse event handling
                val mouseEventModifier = Modifier
                    .onPointerEvent(PointerEventType.Move) {
                        if (exitOnMouseMovementEnabled) {
                            println("Exiting application due to mouse movement")
                            exitApplication()
                        }
                    }
                    .onPointerEvent(PointerEventType.Press) {
                        if (exitOnMouseMovementEnabled) {
                            println("Exiting application due to mouse press")
                            // Commented out to match the behavior in main.kt
                            // exitApplication()
                        }
                    }

                // Add the ShortcutToast composable to show toast notifications
                ShortcutToast(toastState = toastState)

                App(
                    showCitySelectionDialog = showCitySelectionDialog,
                    onCityDialogDismiss = { showCitySelectionDialog = false },
                    onShowCityDialog = { showCitySelectionDialog = true },
                    exitOnMouseMovementEnabled = exitOnMouseMovementEnabled,
                    onExitApplication = { exitApplication() },
                    showHelpDialog = showHelpDialog,
                    onHelpDialogDismiss = { showHelpDialog = false },
                    modifier = mouseEventModifier
                )
        }
    }
}
