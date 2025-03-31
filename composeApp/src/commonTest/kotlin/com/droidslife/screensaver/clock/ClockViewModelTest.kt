package com.droidslife.screensaver.clock

import kotlin.test.Test
import kotlin.test.assertEquals

class ClockViewModelTest {

    @Test
    fun testDefaultClockDesign() {
        val viewModel = ClockViewModel()
        assertEquals(1, viewModel.clockDesign, "Default clock design should be 1")
    }

    @Test
    fun testCycleClockDesign() {
        val viewModel = ClockViewModel()

        // Initial design should be 1
        assertEquals(1, viewModel.clockDesign)

        // Cycle to design 2
        assertEquals(2, viewModel.cycleClockDesign())
        assertEquals(2, viewModel.clockDesign)

        // Cycle to design 3
        assertEquals(3, viewModel.cycleClockDesign())
        assertEquals(3, viewModel.clockDesign)

        // Cycle to design 4
        assertEquals(4, viewModel.cycleClockDesign())
        assertEquals(4, viewModel.clockDesign)

        // Cycle to design 5
        assertEquals(5, viewModel.cycleClockDesign())
        assertEquals(5, viewModel.clockDesign)

        // Cycle to design 6
        assertEquals(6, viewModel.cycleClockDesign())
        assertEquals(6, viewModel.clockDesign)

        // Cycle to design 7
        assertEquals(7, viewModel.cycleClockDesign())
        assertEquals(7, viewModel.clockDesign)

        // Cycle to design 8
        assertEquals(8, viewModel.cycleClockDesign())
        assertEquals(8, viewModel.clockDesign)

        // Cycle back to design 1
        assertEquals(1, viewModel.cycleClockDesign())
        assertEquals(1, viewModel.clockDesign)
    }

    @Test
    fun testUpdateClockDesign() {
        val viewModel = ClockViewModel()

        // Initial design should be 1
        assertEquals(1, viewModel.clockDesign)

        // Update to design 3
        viewModel.updateClockDesign(3)
        assertEquals(3, viewModel.clockDesign)

        // Update to design 2
        viewModel.updateClockDesign(2)
        assertEquals(2, viewModel.clockDesign)

        // Update to design 5 (now valid)
        viewModel.updateClockDesign(5)
        assertEquals(5, viewModel.clockDesign)

        // Update to design 8 (now valid)
        viewModel.updateClockDesign(8)
        assertEquals(8, viewModel.clockDesign)

        // Update to invalid design (should not change)
        viewModel.updateClockDesign(9)
        assertEquals(8, viewModel.clockDesign)

        viewModel.updateClockDesign(0)
        assertEquals(8, viewModel.clockDesign)
    }
}
