package com.droidslife.screensaver.clock

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

/**
 * ViewModel for managing clock design state.
 */
class ClockViewModel {
    private val viewModelScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var autoChangeJob: Job? = null

    /**
     * The current clock design (default is 1).
     */
    var clockDesign by mutableIntStateOf(1)
        private set

    /**
     * Whether auto-changing of clock design is enabled.
     */
    var isAutoChangeEnabled by mutableStateOf(false)
        private set

    /**
     * Whether shuffle mode is enabled for clock design.
     */
    var isShuffleModeEnabled by mutableStateOf(false)
        private set

    /**
     * Cycles through available clock designs (1-8).
     * @return The new clock design after cycling.
     */
    fun cycleClockDesign(): Int {
        // Cycle through designs 1-8
        clockDesign = if (clockDesign >= 8) 1 else clockDesign + 1
        return clockDesign
    }

    /**
     * Updates the clock design to the specified value.
     * @param design The design to set (1-8).
     */
    fun updateClockDesign(design: Int) {
        // Validate and update the design
        if (design in 1..8) {
            clockDesign = design
        }
    }

    /**
     * Toggles auto-changing of clock design.
     * @return The new auto-change state.
     */
    fun toggleAutoChange(): Boolean {
        isAutoChangeEnabled = !isAutoChangeEnabled

        if (isAutoChangeEnabled) {
            startAutoChange()
        } else {
            stopAutoChange()
        }

        return isAutoChangeEnabled
    }

    /**
     * Toggles shuffle mode for clock design.
     * @return The new shuffle mode state.
     */
    fun toggleShuffleMode(): Boolean {
        isShuffleModeEnabled = !isShuffleModeEnabled
        return isShuffleModeEnabled
    }

    /**
     * Starts auto-changing of clock design.
     */
    private fun startAutoChange() {
        stopAutoChange() // Stop any existing job

        autoChangeJob = viewModelScope.launch {
            while (true) {
                delay(10000) // Change every 10 seconds

                if (isShuffleModeEnabled) {
                    // Pick a random design different from the current one
                    var newDesign: Int
                    do {
                        newDesign = Random.nextInt(1, 9)
                    } while (newDesign == clockDesign)

                    updateClockDesign(newDesign)
                } else {
                    cycleClockDesign()
                }
            }
        }
    }

    /**
     * Stops auto-changing of clock design.
     */
    private fun stopAutoChange() {
        autoChangeJob?.cancel()
        autoChangeJob = null
    }
}
