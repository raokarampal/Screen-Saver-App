package com.droidslife.screensaver.clockdigits

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * A modern digital clock design with green gradient background.
 */
@Composable
fun DigitalClockDigit3(
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
                .shadow(8.dp, RoundedCornerShape(24.dp))
                .clip(RoundedCornerShape(24.dp))
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF00AA55), // Dark green
                            Color(0xFF00DD77)  // Light green
                        )
                    )
                )
                .scale(scaleAnimation),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = number.toString(),
                fontSize = 70.sp,
                fontWeight = FontWeight.Light,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(4.dp)
            )
        }
    }
}