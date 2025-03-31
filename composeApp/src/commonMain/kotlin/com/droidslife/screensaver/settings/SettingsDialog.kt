package com.droidslife.screensaver.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

/**
 * Dialog for application settings.
 *
 * @param onDismiss Callback when the dialog is dismissed.
 * @param settings Current settings.
 * @param onThemeToggle Callback when the theme is toggled.
 * @param onClockFormatToggle Callback when the clock format is toggled.
 * @param onAutoPlayToggle Callback when the auto play is toggled.
 * @param onShuffleToggle Callback when the shuffle is toggled.
 * @param onDesignSelected Callback when a design is selected.
 */
@Composable
fun SettingsDialog(
    onDismiss: () -> Unit,
    settings: SettingsModel,
    onThemeToggle: () -> Unit,
    onClockFormatToggle: () -> Unit,
    onAutoPlayToggle: () -> Unit,
    onShuffleToggle: () -> Unit,
    onDesignSelected: (Int) -> Unit
) {
    var showShortcutsDialog by remember { mutableStateOf(false) }

    if (showShortcutsDialog) {
        ShortcutsHelpDialog(onDismiss = { showShortcutsDialog = false })
    }
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Text(
                    text = "Settings",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 16.dp)
                )


                // Theme section
                Text(
                    text = "Theme",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Dark Theme",
                        modifier = Modifier.weight(1f)
                    )
                    Switch(
                        checked = settings.isDarkTheme,
                        onCheckedChange = { onThemeToggle() }
                    )
                }

                Divider(modifier = Modifier.padding(vertical = 8.dp))

                // Clock format section
                Text(
                    text = "Clock Format",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = !settings.is24HourFormat,
                        onClick = { if (settings.is24HourFormat) onClockFormatToggle() }
                    )
                    Text(
                        text = "12-hour (AM/PM)",
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = settings.is24HourFormat,
                        onClick = { if (!settings.is24HourFormat) onClockFormatToggle() }
                    )
                    Text(
                        text = "24-hour",
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }

                Divider(modifier = Modifier.padding(vertical = 8.dp))

                // Weather section
                Text(
                    text = "Weather",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )

                // Current city (read-only)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Current City",
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = settings.currentCity ?: "Auto (Timezone)",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Divider(modifier = Modifier.padding(vertical = 8.dp))

                // Playback section
                Text(
                    text = "Playback",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )

                // Auto play toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Auto Play",
                        modifier = Modifier.weight(1f)
                    )
                    Switch(
                        checked = settings.autoPlayEnabled,
                        onCheckedChange = { onAutoPlayToggle() }
                    )
                }

                // Shuffle toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Shuffle",
                        modifier = Modifier.weight(1f)
                    )
                    Switch(
                        checked = settings.shuffleEnabled,
                        onCheckedChange = { onShuffleToggle() }
                    )
                }

                Divider(modifier = Modifier.padding(vertical = 8.dp))

                // Design section
                Text(
                    text = "Design",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )

                // Design selection
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = settings.selectedDesignId == 0,
                        onClick = { onDesignSelected(0) }
                    )
                    Text(
                        text = "Classic",
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = settings.selectedDesignId == 1,
                        onClick = { onDesignSelected(1) }
                    )
                    Text(
                        text = "Modern",
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = settings.selectedDesignId == 2,
                        onClick = { onDesignSelected(2) }
                    )
                    Text(
                        text = "Minimalist",
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Close button
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Close")
                }
            }
        }
    }
}

/**
 * Dialog that displays all available keyboard shortcuts in the app.
 *
 * @param onDismiss Callback when the dialog is dismissed.
 * @param onExitApplication Callback to exit the application.
 */
@Composable
fun ShortcutsHelpDialog(
    onDismiss: () -> Unit,
    onExitApplication: () -> Unit = {}
) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
        ) {
            // Exit button in top right corner
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 4.dp, end = 4.dp)
            ) {
                Button(
                    onClick = onExitApplication,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp)),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Exit Application",
                        modifier = Modifier.padding(end = 4.dp)
                    )
                    Text(
                        text = "Exit App",
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Text(
                    text = "Keyboard Shortcuts",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // List of shortcuts
                Text(
                    text = "Available Shortcuts",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )

                // Ctrl + C shortcut
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 4.dp)
                ) {
                    Text(
                        text = "Ctrl + C",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.width(80.dp)
                    )
                    Text(text = "Open Settings")
                }

                // Ctrl + H shortcut
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 4.dp)
                ) {
                    Text(
                        text = "Ctrl + H",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.width(80.dp)
                    )
                    Text(text = "Show Help")
                }

                // Ctrl + N shortcut
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 4.dp)
                ) {
                    Text(
                        text = "Ctrl + N",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.width(80.dp)
                    )
                    Text(text = "Cycle Clock Design")
                }

                // Ctrl + P shortcut
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 4.dp)
                ) {
                    Text(
                        text = "Ctrl + P",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.width(80.dp)
                    )
                    Text(text = "Toggle Auto-Change")
                }

                // Ctrl + R shortcut
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 4.dp)
                ) {
                    Text(
                        text = "Ctrl + R",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.width(80.dp)
                    )
                    Text(text = "Toggle Shuffle Mode")
                }

                // Ctrl + T shortcut
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 4.dp)
                ) {
                    Text(
                        text = "Ctrl + T",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.width(80.dp)
                    )
                    Text(text = "Toggle Theme (Light/Dark)")
                }

                // Ctrl + S shortcut
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 4.dp)
                ) {
                    Text(
                        text = "Ctrl + S",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.width(80.dp)
                    )
                    Text(text = "Show City Selection")
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Theme settings
                Text(
                    text = "Theme Settings",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )

                Text(
                    text = "Toggle between light and dark theme using Ctrl + T or in the Settings dialog (Ctrl + C)",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 4.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Close button
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Close")
                }
            }
        }
    }
}
