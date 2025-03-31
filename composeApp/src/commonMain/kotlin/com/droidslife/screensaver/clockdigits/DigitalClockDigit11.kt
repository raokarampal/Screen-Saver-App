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
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.Font
import screen_saver_app.composeapp.generated.resources.Res
import screen_saver_app.composeapp.generated.resources.Technology_Italic

/**
 * A tech-inspired clock design using Technology font with background-colored box and onBackground border.
 */
@Composable
fun DigitalClockDigit11(
    number: Int,
    modifier: Modifier = Modifier,
    durationMillis: Int = 500,
) {
    var oldNumber by remember { mutableStateOf(number) }
    var scale by remember { mutableStateOf(1f) }

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
    
    // Get colors from the theme
    val backgroundColor = MaterialTheme.colorScheme.background
    val onBackgroundColor = MaterialTheme.colorScheme.onBackground

    Column {
        Box(
            modifier = Modifier
                .size(110.dp, 130.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(backgroundColor)
                .border(1.dp, onBackgroundColor, RoundedCornerShape(16.dp))
                .scale(scaleAnimation),
            contentAlignment = Alignment.Center
        ) {
            // Tech-inspired display
            Box(
                modifier = Modifier
                    .size(100.dp, 120.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(backgroundColor)
                    .border(1.dp, onBackgroundColor, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                // Tech-inspired digit
                Text(
                    text = number.toString(),
                    fontSize = 80.sp,
                    fontFamily = technologyFont,
                    color = onBackgroundColor, // Use onBackground color for text
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(4.dp)
                )
            }
        }
    }
}