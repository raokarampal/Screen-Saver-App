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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.Font
import screen_saver_app.composeapp.generated.resources.Res
import screen_saver_app.composeapp.generated.resources.Orbitron_Bold

/**
 * A futuristic digital clock design inspired by Orbitron font.
 */
@Composable
fun DigitalClockDigit7(
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

    // Use actual Orbitron font
    val orbitronFont = FontFamily(Font(Res.font.Orbitron_Bold))

    Column {
        Box(
            modifier = Modifier
                .size(110.dp, 130.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF1A237E), // Dark blue
                            Color(0xFF303F9F)  // Medium blue
                        )
                    )
                )
                .border(1.dp, Color(0xFF3F51B5), RoundedCornerShape(8.dp))
                .scale(scaleAnimation),
            contentAlignment = Alignment.Center
        ) {
            // Futuristic display
            Box(
                modifier = Modifier
                    .size(100.dp, 120.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFF1A237E), // Dark blue
                                Color(0xFF0D1442)  // Darker blue
                            )
                        )
                    )
                    .border(1.dp, Color(0xFF5C6BC0), RoundedCornerShape(6.dp)),
                contentAlignment = Alignment.Center
            ) {
                // Futuristic digit
                Text(
                    text = number.toString(),
                    fontSize = 80.sp,
                    fontFamily = orbitronFont,
                    color = Color(0xFF64FFDA), // Teal/cyan color for futuristic look
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(4.dp)
                )
            }
        }
    }
}