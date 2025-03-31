package com.droidslife.screensaver

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.droidslife.screensaver.settings.SettingsViewModel
import com.droidslife.screensaver.theme.AppTheme
import kotlinx.datetime.Instant
import kotlinx.datetime.format
import kotlinx.datetime.format.DateTimeComponents
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.byUnicodePattern
import org.koin.compose.KoinContext
import org.koin.compose.koinInject

@Composable
internal fun App(
    showCitySelectionDialog: Boolean = false, 
    onCityDialogDismiss: () -> Unit = {},
    onShowCityDialog: () -> Unit = {},
    exitOnMouseMovementEnabled: Boolean = true,
    onExitApplication: () -> Unit = {},
    showHelpDialog: Boolean = false,
    onHelpDialogDismiss: () -> Unit = {},
    modifier: Modifier = Modifier
) = KoinContext {
        // Get the settings view model to access the theme preference
        val settingsViewModel = koinInject<SettingsViewModel>()

        AppTheme(isDark = settingsViewModel.settings.isDarkTheme) {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.safeDrawing)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                DigitalClockApp(
                    showCitySelectionDialog = showCitySelectionDialog, 
                    onCityDialogDismiss = onCityDialogDismiss, 
                    onShowCityDialog = onShowCityDialog,
                    exitOnMouseMovementEnabled = exitOnMouseMovementEnabled,
                    onExitApplication = onExitApplication,
                    showHelpDialog = showHelpDialog,
                    onHelpDialogDismiss = onHelpDialogDismiss,
                    onShowHelpDialog = { /* This is a no-op because we can't set showHelpDialog to true here */ }
                )
            }
        }
    }

@OptIn(FormatStringsInDatetimeFormats::class)
private fun Instant.dateFormat(format:String? =null): String {
    return format(DateTimeComponents.Format {
        byUnicodePattern(format?:"dd/MM/yyyy")
    })
}
