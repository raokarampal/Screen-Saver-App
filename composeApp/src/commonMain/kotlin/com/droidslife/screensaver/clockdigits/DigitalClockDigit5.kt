package com.droidslife.screensaver.clockdigits

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * A retro monochrome LCD digital watch design.
 */
@Composable
fun DigitalClockDigit5(
    number: Int,
    modifier: Modifier = Modifier,
    durationMillis: Int = 500,
) {
    var oldNumber by remember { mutableStateOf(number) }
    var scale by remember { mutableStateOf(1f) }

    // Animate scale when number changes
    LaunchedEffect(number) {
        if (oldNumber != number) {
            scale = 1.1f
            oldNumber = number
        }
    }

    val scaleAnimation by animateFloatAsState(
        targetValue = scale,
        animationSpec = tween(durationMillis = durationMillis),
        finishedListener = {
            scale = 1f
        }
    )

    Column {
        Box(
            modifier = Modifier
                .size(110.dp, 130.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(
                    color = Color(0xFF9BA29F) // LCD background color (grayish)
                )
                .border(1.dp, Color(0xFF333333), RoundedCornerShape(4.dp)) // Dark border
                .scale(scaleAnimation),
            contentAlignment = Alignment.Center
        ) {
            // LCD display background
            Box(
                modifier = Modifier
                    .size(100.dp, 120.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color(0xFFCCDDCC)) // LCD screen color (light greenish)
                    .border(1.dp, Color(0xFF555555), RoundedCornerShape(2.dp)),
                contentAlignment = Alignment.Center
            ) {
                // LCD digit
                Text(
                    text = number.toString(),
                    fontSize = 70.sp,
                    fontFamily = FontFamily.Monospace, // Typical for LCD displays
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF101C14), // Dark LCD segments
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(4.dp)
                )
            }
        }
    }
}