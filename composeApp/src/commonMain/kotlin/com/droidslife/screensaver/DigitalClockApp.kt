package com.droidslife.screensaver

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.zIndex
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import com.droidslife.screensaver.clock.ClockViewModel
import com.droidslife.screensaver.clockdigits.DigitalClockDigit
import com.droidslife.screensaver.clockdigits.DigitalClockDigit2
import com.droidslife.screensaver.clockdigits.DigitalClockDigit3
import com.droidslife.screensaver.clockdigits.DigitalClockDigit4
import com.droidslife.screensaver.clockdigits.DigitalClockDigit5
import com.droidslife.screensaver.clockdigits.DigitalClockDigit6
import com.droidslife.screensaver.clockdigits.DigitalClockDigit7
import com.droidslife.screensaver.clockdigits.DigitalClockDigit8
import com.droidslife.screensaver.clockdigits.DigitalClockDigit9
import com.droidslife.screensaver.clockdigits.DigitalClockDigit10
import com.droidslife.screensaver.clockdigits.DigitalClockDigit11
import com.droidslife.screensaver.settings.SettingsDialog
import com.droidslife.screensaver.settings.ShortcutsHelpDialog
import com.droidslife.screensaver.settings.SettingsViewModel
import com.droidslife.screensaver.weather.CitySearchState
import com.droidslife.screensaver.weather.WeatherState
import com.droidslife.screensaver.weather.WeatherViewModel
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.compose.koinInject

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun DigitalClockApp(
    showCitySelectionDialog: Boolean = false,
    onCityDialogDismiss: () -> Unit = {},
    onShowCityDialog: () -> Unit = {},
    exitOnMouseMovementEnabled: Boolean = true,
    onExitApplication: () -> Unit = {},
    showHelpDialog: Boolean = false,
    onHelpDialogDismiss: () -> Unit = {},
    onShowHelpDialog: () -> Unit = {}
) {
    val time by produceState(initialValue = Clock.System.now()) {
        while (true) {
            delay(1000L)
            value = Clock.System.now()
        }
    }

    val weatherViewModel = koinInject<WeatherViewModel>()
    val weatherState = weatherViewModel.state

    // Local state for city search dialog
    var searchQuery by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }

    // Focus requester for the main box to capture keyboard events
    val mainBoxFocusRequester = remember { FocusRequester() }

    // Use the ClockViewModel to manage clock design state
    val clockViewModel = koinInject<ClockViewModel>()

    // Use the SettingsViewModel to manage settings
    val settingsViewModel = koinInject<SettingsViewModel>()

    // Help dialog state is now passed as a parameter

    // Convert to LocalDateTime using the timezone from the selected city if available
    val timezone = when (weatherState) {
        is WeatherState.Success -> {
            try {
                TimeZone.of(weatherState.weatherData.location.tzId)
            } catch (e: Exception) {
                TimeZone.currentSystemDefault()
            }
        }
        else -> TimeZone.currentSystemDefault()
    }
    val localDateTime = time.toLocalDateTime(timezone)

    // Create a simple modifier for the main box
    val modifier = Modifier

    // Show settings dialog if requested
    if (settingsViewModel.isSettingsDialogOpen) {
        SettingsDialog(
            onDismiss = { settingsViewModel.closeSettingsDialog() },
            settings = settingsViewModel.settings,
            onThemeToggle = { settingsViewModel.toggleTheme() },
            onClockFormatToggle = { settingsViewModel.toggleClockFormat() },
            onAutoPlayToggle = { settingsViewModel.toggleAutoPlay() },
            onShuffleToggle = { settingsViewModel.toggleShuffle() },
            onDesignSelected = { settingsViewModel.setSelectedDesign(it) }
        )
    }

    // Show help dialog if requested
    if (showHelpDialog) {
        ShortcutsHelpDialog(
            onDismiss = onHelpDialogDismiss,
            onExitApplication = onExitApplication
        )
    }

    // Show city selection dialog if requested
    if (showCitySelectionDialog) {
        Dialog(onDismissRequest = {
            searchQuery = ""
            onCityDialogDismiss()
        }) {
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
                    Text(
                        text = "Select City",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Remember the last search job to cancel it when a new search is started
                    var searchJob by remember { mutableStateOf<kotlinx.coroutines.Job?>(null) }

                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { 
                            searchQuery = it
                            // Cancel the previous search job if it's still active
                            searchJob?.cancel()
                            // Start a new search job with a delay to debounce rapid typing
                            searchJob = kotlinx.coroutines.MainScope().launch {
                                delay(300) // 300ms debounce delay
                                weatherViewModel.searchCities(it)
                            }
                        },
                        label = { Text("Search city") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(
                            onSearch = {
                                // Cancel any pending search job
                                searchJob?.cancel()
                                // Perform the search immediately
                                weatherViewModel.searchCities(searchQuery)
                            }
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    when (val citySearchState = weatherViewModel.citySearchState) {
                        is CitySearchState.Loading -> {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        is CitySearchState.Success -> {
                            LazyColumn(
                                modifier = Modifier.weight(1f, fill = false)
                            ) {
                                items(citySearchState.cities) { city ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                weatherViewModel.selectCity(city)
                                                searchQuery = ""
                                                onCityDialogDismiss()
                                            }
                                            .padding(vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "${city.name}, ${city.region}, ${city.country}",
                                            style = MaterialTheme.typography.bodyLarge
                                        )
                                    }
                                }
                            }
                        }
                        is CitySearchState.Empty -> {
                            Text(
                                text = "No cities found",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                        is CitySearchState.Error -> {
                            Text(
                                text = "Error: ${citySearchState.message}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Red,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                        else -> {
                            Text(
                                text = "Type to search for a city",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }

                    Button(
                        onClick = {
                            searchQuery = ""
                            onCityDialogDismiss()
                        },
                        modifier = Modifier.padding(top = 16.dp)
                    ) {
                        Text("Cancel")
                    }
                }
            }

            LaunchedEffect(Unit) {
                try {
                    // Add a small delay to ensure the dialog is fully rendered before requesting focus
                    delay(100)
                    focusRequester.requestFocus()
                } catch (e: Exception) {
                    // Log the exception but don't crash the app
                    println("Error requesting focus: ${e.message}")
                }
            }
        }
    }

    // Request focus for the main box to capture keyboard events
    LaunchedEffect(Unit) {
        mainBoxFocusRequester.requestFocus()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .focusRequester(mainBoxFocusRequester),
        contentAlignment = Alignment.Center
    ) {

        // Main content with animated mesh gradient background for TV-like ambient effect
        // Use Animatable for smoother, continuous animations
        var colorPhase by remember { mutableStateOf(0f) }

        // Use Animatable for the center point to enable smoother, continuous animation
        val centerYAnimatable = remember { Animatable(0.5f) }

        // Create animated RGB color values
        val redAnimatable = remember { Animatable(0f) }
        val greenAnimatable = remember { Animatable(0f) }
        val blueAnimatable = remember { Animatable(0f) }

        // Launch animation for the center point and colors
        LaunchedEffect(Unit) {
            // Animate the center point in a continuous loop with smooth transitions
            launch {
                while (true) {
                    // Use a smoother animation with easing for more gradual transitions
                    centerYAnimatable.animateTo(
                        targetValue = 0.4f,
                        animationSpec = tween(
                            durationMillis = 5000,
                            easing = androidx.compose.animation.core.CubicBezierEasing(0.4f, 0.0f, 0.2f, 1.0f)
                        )
                    )
                    centerYAnimatable.animateTo(
                        targetValue = 0.6f,
                        animationSpec = tween(
                            durationMillis = 6000,
                            easing = androidx.compose.animation.core.CubicBezierEasing(0.4f, 0.0f, 0.2f, 1.0f)
                        )
                    )
                    centerYAnimatable.animateTo(
                        targetValue = 0.5f,
                        animationSpec = tween(
                            durationMillis = 4000,
                            easing = androidx.compose.animation.core.CubicBezierEasing(0.4f, 0.0f, 0.2f, 1.0f)
                        )
                    )
                }
            }

            // Animate the RGB color phase
            launch {
                while (true) {
                    // Animate red component
                    redAnimatable.animateTo(
                        targetValue = 1f,
                        animationSpec = tween(durationMillis = 8000)
                    )
                    redAnimatable.animateTo(
                        targetValue = 0f,
                        animationSpec = tween(durationMillis = 8000)
                    )
                }
            }

            launch {
                delay(2700) // Offset to create interesting color combinations
                while (true) {
                    // Animate green component
                    greenAnimatable.animateTo(
                        targetValue = 1f,
                        animationSpec = tween(durationMillis = 7000)
                    )
                    greenAnimatable.animateTo(
                        targetValue = 0f,
                        animationSpec = tween(durationMillis = 7000)
                    )
                }
            }

            launch {
                delay(5300) // Offset to create interesting color combinations
                while (true) {
                    // Animate blue component
                    blueAnimatable.animateTo(
                        targetValue = 1f,
                        animationSpec = tween(durationMillis = 9000)
                    )
                    blueAnimatable.animateTo(
                        targetValue = 0f,
                        animationSpec = tween(durationMillis = 9000)
                    )
                }
            }
        }

        // Get colors from the current theme
        val primaryColor = MaterialTheme.colorScheme.primary
        val secondaryColor = MaterialTheme.colorScheme.secondary
        val surfaceColor = MaterialTheme.colorScheme.surface

        // Box with shadow and content
        Box(
            modifier = Modifier
                .padding(24.dp), // Padding for the overall container
            contentAlignment = Alignment.Center
        ) {
            // TV-like ambient light background with RGB effects
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .offset(y = 16.dp, x = 6.dp) // Increased offset for a more pronounced shadow
                    .zIndex(0f) // Place behind the content
                    .clip(RoundedCornerShape(32.dp)) // Rounded corners for the gradient

            )

            // Actual card with mesh gradient background (positioned on top with zIndex)
            Box(
                modifier = Modifier
                    .zIndex(1f) // Place on top of the shadow
                    .clip(RoundedCornerShape(24.dp))
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Time display with AM/PM in superscript
                val is24Hour = settingsViewModel.settings.is24HourFormat
                val hour = if (is24Hour) localDateTime.hour else localDateTime.hour % 12
                val hourDisplay = if (!is24Hour && hour == 0) 12 else hour
                val minute = localDateTime.minute
                val isAm = localDateTime.hour < 12

                // Display the appropriate clock design based on the clockViewModel.clockDesign state
                when (clockViewModel.clockDesign) {
                    1 -> {
                        // Original flip clock design
                        DigitalClock(hourDisplay, minute)
                    }
                    2 -> {
                        // Second flip clock design (using DigitalClockDigit2)
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            DigitalClockDigit2((hourDisplay / 10) % 10)
                            DigitalClockDigit2(hourDisplay % 10)
                            Spacer(modifier = Modifier.width(8.dp))
                            DigitalClockDigit2((minute / 10) % 10)
                            DigitalClockDigit2(minute % 10)
                        }
                    }
                    3 -> {
                        // Third flip clock design
                        DigitalClock3(hourDisplay, minute)
                    }
                    4 -> {
                        // Fourth flip clock design
                        DigitalClock4(hourDisplay, minute)
                    }
                    5 -> {
                        // Retro LCD digital watch design
                        DigitalClock5(hourDisplay, minute)
                    }
                    6 -> {
                        // Seven Segment digital clock design
                        DigitalClock6(hourDisplay, minute)
                    }
                    7 -> {
                        // Futuristic Orbitron-inspired clock design
                        DigitalClock7(hourDisplay, minute)
                    }
                    8 -> {
                        // Handwritten IndieFlower-inspired clock design
                        DigitalClock8(hourDisplay, minute)
                    }
                    9 -> {
                        // Seven Segment design with background-colored box and onBackground border
                        DigitalClock9(hourDisplay, minute)
                    }
                    10 -> {
                        // Orbitron design with background-colored box and onBackground border
                        DigitalClock10(hourDisplay, minute)
                    }
                    11 -> {
                        // Technology design with background-colored box and onBackground border
                        DigitalClock11(hourDisplay, minute)
                    }
                }

                // AM/PM indicator (only show for 12-hour format)
                if (!settingsViewModel.settings.is24HourFormat) {
                    Text(
                        text = if (isAm) "AM" else "PM",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                        ),
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                // Add spacer between clock design and weather component
                Spacer(modifier = Modifier.height(16.dp))

                // Location and weather info
                when (weatherState) {
                    is WeatherState.Loading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    is WeatherState.Success -> {
                        val data = weatherState.weatherData
                        val location = weatherState.location

                        // City name in uppercase
                        Text(
                            text = data.location.name.uppercase(),
                            style = MaterialTheme.typography.titleLarge.copy(
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Bold
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Weather info
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            // Weather icon would go here in a real implementation
                            // For now, just show the temperature
                            Text(
                                text = "${data.current.tempC.toInt()}°C",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            // Weather description
                            Text(
                                text = data.current.condition.text,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                                )
                            )
                        }
                    }
                    is WeatherState.Error -> {
                        Text(
                            text = "Error loading weather data",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.error
                            )
                        )
                    }
                }
            }
        }

        // Control icons row with tooltips - moved to bottom center
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 16.dp, start = 16.dp, end = 16.dp)
        ) {
            // Center group of buttons
            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Auto-change icon (Play/Pause) with tooltip
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                            .clickable { clockViewModel.toggleAutoChange() }
                            .padding(4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (clockViewModel.isAutoChangeEnabled) Icons.Filled.Close else Icons.Filled.PlayArrow,
                            contentDescription = if (clockViewModel.isAutoChangeEnabled) "Pause auto-change" else "Start auto-change",
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = if (clockViewModel.isAutoChangeEnabled) 1f else 0.6f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Text(
                        text = if (clockViewModel.isAutoChangeEnabled) "Stop" else "Auto",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                            fontSize = 10.sp
                        ),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                // Shuffle mode icon with tooltip
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                            .clickable { clockViewModel.toggleShuffleMode() }
                            .padding(4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Refresh,
                            contentDescription = "Toggle shuffle mode",
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = if (clockViewModel.isShuffleModeEnabled) 1f else 0.6f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Text(
                        text = "Shuffle",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                            fontSize = 10.sp
                        ),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                // City change icon with tooltip
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                            .clickable { 
                                // Call the new callback to show the city dialog
                                onShowCityDialog()
                            }
                            .padding(4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.LocationOn,
                            contentDescription = "Change city",
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Text(
                        text = "City",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                            fontSize = 10.sp
                        ),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                // Settings icon with tooltip
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                            .clickable { 
                                settingsViewModel.openSettingsDialog()
                            }
                            .padding(4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = "Settings",
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Text(
                        text = "Settings",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                            fontSize = 10.sp
                        ),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                // Help icon with tooltip
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                            .clickable { 
                                onShowHelpDialog()
                            }
                            .padding(4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "?",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            textAlign = TextAlign.Center
                        )
                    }
                    Text(
                        text = "Help",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                            fontSize = 10.sp
                        ),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}
}

@Composable
fun DigitalClock(hour: Int, minute: Int) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        DigitalClockDigit((hour / 10) % 10)
        DigitalClockDigit(hour % 10)
        Spacer(modifier = Modifier.width(8.dp))
        DigitalClockDigit((minute / 10) % 10)
        DigitalClockDigit(minute % 10)
    }
}

@Composable
fun DigitalClock3(hour: Int, minute: Int) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        DigitalClockDigit3((hour / 10) % 10)
        DigitalClockDigit3(hour % 10)
        Spacer(modifier = Modifier.width(8.dp))
        DigitalClockDigit3((minute / 10) % 10)
        DigitalClockDigit3(minute % 10)
    }
}

@Composable
fun DigitalClock4(hour: Int, minute: Int) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        DigitalClockDigit4((hour / 10) % 10)
        DigitalClockDigit4(hour % 10)
        Spacer(modifier = Modifier.width(8.dp))
        DigitalClockDigit4((minute / 10) % 10)
        DigitalClockDigit4(minute % 10)
    }
}

@Composable
fun DigitalClock5(hour: Int, minute: Int) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        DigitalClockDigit5((hour / 10) % 10)
        DigitalClockDigit5(hour % 10)
        Spacer(modifier = Modifier.width(8.dp))
        DigitalClockDigit5((minute / 10) % 10)
        DigitalClockDigit5(minute % 10)
    }
}

@Composable
fun DigitalClock6(hour: Int, minute: Int) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        DigitalClockDigit6((hour / 10) % 10)
        DigitalClockDigit6(hour % 10)
        Spacer(modifier = Modifier.width(8.dp))
        DigitalClockDigit6((minute / 10) % 10)
        DigitalClockDigit6(minute % 10)
    }
}

@Composable
fun DigitalClock7(hour: Int, minute: Int) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        DigitalClockDigit7((hour / 10) % 10)
        DigitalClockDigit7(hour % 10)
        Spacer(modifier = Modifier.width(8.dp))
        DigitalClockDigit7((minute / 10) % 10)
        DigitalClockDigit7(minute % 10)
    }
}

@Composable
fun DigitalClock8(hour: Int, minute: Int) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        DigitalClockDigit8((hour / 10) % 10)
        DigitalClockDigit8(hour % 10)
        Spacer(modifier = Modifier.width(8.dp))
        DigitalClockDigit8((minute / 10) % 10)
        DigitalClockDigit8(minute % 10)
    }
}

@Composable
fun DigitalClock9(hour: Int, minute: Int) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        DigitalClockDigit9((hour / 10) % 10)
        DigitalClockDigit9(hour % 10)
        Spacer(modifier = Modifier.width(8.dp))
        DigitalClockDigit9((minute / 10) % 10)
        DigitalClockDigit9(minute % 10)
    }
}

@Composable
fun DigitalClock10(hour: Int, minute: Int) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        DigitalClockDigit10((hour / 10) % 10)
        DigitalClockDigit10(hour % 10)
        Spacer(modifier = Modifier.width(8.dp))
        DigitalClockDigit10((minute / 10) % 10)
        DigitalClockDigit10(minute % 10)
    }
}

@Composable
fun DigitalClock11(hour: Int, minute: Int) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        DigitalClockDigit11((hour / 10) % 10)
        DigitalClockDigit11(hour % 10)
        Spacer(modifier = Modifier.width(8.dp))
        DigitalClockDigit11((minute / 10) % 10)
        DigitalClockDigit11(minute % 10)
    }
}
