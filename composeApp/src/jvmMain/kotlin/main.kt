import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.*
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.window.*
import com.droidslife.screensaver.App
import com.droidslife.screensaver.clock.ClockViewModel
import com.droidslife.screensaver.components.KeyEventAction
import com.droidslife.screensaver.components.KeyEventHandler
import com.droidslife.screensaver.components.KeyEventState
import com.droidslife.screensaver.components.ShortcutToast
import com.droidslife.screensaver.components.rememberKeyEventHandler
import com.droidslife.screensaver.components.rememberToastState
import com.droidslife.screensaver.di.appModule
import com.droidslife.screensaver.di.initKoin
import com.droidslife.screensaver.settings.SettingsViewModel
import com.droidslife.screensaver.weather.WeatherViewModel
import kotlinx.coroutines.delay
import org.koin.compose.KoinApplication
import org.koin.compose.koinInject

@OptIn(ExperimentalComposeUiApi::class)
fun main() = application {
    initKoin {
        modules(appModule)
    }

    // Use the ClockViewModel to manage clock design state
    val clockViewModel = koinInject<ClockViewModel>()

    // Use the WeatherViewModel for weather data and city selection
    val weatherViewModel = koinInject<WeatherViewModel>()

    // Use the SettingsViewModel to manage settings
    val settingsViewModel = koinInject<SettingsViewModel>()

    // State for showing city selection dialog
    var showCitySelectionDialog by remember { mutableStateOf(false) }

    // State for tracking whether exit on mouse movement is enabled
    var exitOnMouseMovementEnabled by remember { mutableStateOf(true) }

    // State for showing help dialog
    var showHelpDialog by remember { mutableStateOf(false) }

    // Create toast state for showing shortcut notifications
    val toastState = rememberToastState()

    // Create key event state
    val keyEventState = KeyEventState(
        exitOnMouseMovementEnabled = exitOnMouseMovementEnabled
    )

    // Define the onAction callback to handle key events and show toasts directly
    val onAction: (KeyEventAction) -> Unit = { action ->
        when (action) {
            is KeyEventAction.CycleClockDesign -> {
                clockViewModel.cycleClockDesign()
                println("Clock design changed to: ${clockViewModel.clockDesign}")
                toastState.show("Ctrl + N")
            }
            is KeyEventAction.ToggleAutoChange -> {
                val isEnabled = clockViewModel.toggleAutoChange()
                println("Auto-change ${if (isEnabled) "enabled" else "disabled"}")
                toastState.show("Ctrl + P")
            }
            is KeyEventAction.ToggleShuffle -> {
                val isEnabled = clockViewModel.toggleShuffleMode()
                println("Shuffle mode ${if (isEnabled) "enabled" else "disabled"}")
                toastState.show("Ctrl + R")
            }
            is KeyEventAction.ShowCityDialog -> {
                showCitySelectionDialog = true
                println("City selection dialog shown")
                toastState.show("Ctrl + S")
            }
            is KeyEventAction.ExitApplication -> {
                println("Exiting application via Ctrl+X shortcut")
                toastState.show("Ctrl + X")
                exitApplication()
            }
            is KeyEventAction.ToggleExitOnMouseMovement -> {
                exitOnMouseMovementEnabled = !exitOnMouseMovementEnabled
                println("Exit on mouse movement ${if (exitOnMouseMovementEnabled) "enabled" else "disabled"}")
                toastState.show("Ctrl + Z")
            }
            is KeyEventAction.OpenSettings -> {
                settingsViewModel.openSettingsDialog()
                toastState.show("Ctrl + C")
            }
            is KeyEventAction.ShowHelp -> {
                showHelpDialog = true
                toastState.show("Ctrl + H")
            }
            is KeyEventAction.ToggleTheme -> {
                settingsViewModel.toggleTheme()
                toastState.show("Ctrl + T")
            }
            is KeyEventAction.ShowToast -> {
                toastState.show(action.message)
            }
        }
    }

    // Create key event handler with the updated onAction callback
    val keyEventHandler = rememberKeyEventHandler(
        state = keyEventState,
        onAction = onAction
    )

    Window(
        title = "Screen Saver App",
        state = rememberWindowState(
            placement = WindowPlacement.Fullscreen,
            position = WindowPosition(Alignment.Center),
            ),
        onCloseRequest = ::exitApplication,
        resizable = false,
        alwaysOnTop = true,
        undecorated = true,
        onKeyEvent = { event -> 
            // Use the key event handler to handle key events
            keyEventHandler.handleWindowKeyEvent(event)
        }
    ) {
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
                        // Commented out to match the behavior in DigitalClockApp.kt
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

@Preview
@Composable
fun AppPreview() { 
    KoinApplication(application = {}) {
        App(
            showCitySelectionDialog = false, 
            onCityDialogDismiss = {},
            onShowCityDialog = {},
            showHelpDialog = false,
            onHelpDialogDismiss = {}
        ) 
    }
}
