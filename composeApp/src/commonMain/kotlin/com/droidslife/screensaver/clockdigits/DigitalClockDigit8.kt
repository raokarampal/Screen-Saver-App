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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.Font
import screen_saver_app.composeapp.generated.resources.Res
import screen_saver_app.composeapp.generated.resources.Technology_Italic

/**
 * A tech-inspired clock design using Technology font.
 */
@Composable
fun DigitalClockDigit8(
    number: Int,
    modifier: Modifier = Modifier,
    durationMillis: Int = 500,
) {
    var oldNumber by remember { mutableStateOf(number) }
    var scale by remember { mutableStateOf(1f) }
    val rotation = remember { (-5..5).random().toFloat() } // Random slight rotation for handwritten feel

    // Animate scale when number changes
    LaunchedEffect(number) {
        if (oldNumber != number) {
            scale = 1.2f
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

    // Use actual Technology font
    val technologyFont = FontFamily(Font(Res.font.Technology_Italic))

    Column {
        Box(
            modifier = Modifier
                .size(110.dp, 130.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(
                    color = Color(0xFFFFF9C4) // Light yellow like a sticky note
                )
                .border(2.dp, Color(0xFFFFEB3B), RoundedCornerShape(12.dp))
                .scale(scaleAnimation)
                .rotate(rotation), // Apply slight random rotation
            contentAlignment = Alignment.Center
        ) {
            // Handwritten style display
            Box(
                modifier = Modifier
                    .size(100.dp, 120.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFFFF9C4)) // Same light yellow
                    .padding(4.dp),
                contentAlignment = Alignment.Center
            ) {
                // Handwritten style digit
                Text(
                    text = number.toString(),
                    fontSize = 80.sp,
                    fontFamily = technologyFont,
                    color = Color(0xFF673AB7), // Purple for contrast on yellow
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(4.dp)
                )
            }
        }
    }
}