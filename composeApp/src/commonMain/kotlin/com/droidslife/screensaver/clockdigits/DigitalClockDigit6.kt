package com.droidslife.screensaver.clockdigits

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import org.jetbrains.compose.resources.Font
import screen_saver_app.composeapp.generated.resources.Res
import screen_saver_app.composeapp.generated.resources.Seven_Segment

/**
 * A digital clock design using Seven Segment font.
 */
@Composable
fun DigitalClockDigit6(
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

    // Use actual Seven Segment font
    val sevenSegmentFont = FontFamily(Font(Res.font.Seven_Segment))

    Column {
        Box(
            modifier = Modifier
                .size(110.dp, 130.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(
                    color = Color(0xFF000000) // Black background
                )
                .border(1.dp, Color(0xFF333333), RoundedCornerShape(4.dp))
                .scale(scaleAnimation),
            contentAlignment = Alignment.Center
        ) {
            // Digital display
            Box(
                modifier = Modifier
                    .size(100.dp, 120.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color(0xFF000000)) // Black background
                    .border(1.dp, Color(0xFF555555), RoundedCornerShape(2.dp)),
                contentAlignment = Alignment.Center
            ) {
                // For digit 1, we create a group that's right-aligned internally but centered as a whole
                if (number == 1) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier.width(50.dp),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            // Background digit 8 with lower opacity
                            Text(
                                text = "8",
                                fontSize = 80.sp,
                                fontFamily = sevenSegmentFont,
                                color = Color(0xFFFF0000).copy(alpha = 0.10f), // Red color with 10% opacity
                                textAlign = TextAlign.End,
                                modifier = Modifier.padding(4.dp)
                            )

                            // Seven Segment digit
                            Text(
                                text = number.toString(),
                                fontSize = 80.sp,
                                fontFamily = sevenSegmentFont,
                                color = Color(0xFFFF0000), // Red color typical for seven segment displays
                                textAlign = TextAlign.End,
                                modifier = Modifier.padding(4.dp)
                            )
                        }
                    }
                } else {
                    // Background digit 8 with lower opacity
                    Text(
                        text = "8",
                        fontSize = 80.sp,
                        fontFamily = sevenSegmentFont,
                        color = Color(0xFFFF0000).copy(alpha = 0.10f), // Red color with 10% opacity
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(4.dp)
                    )

                    // Seven Segment digit
                    Text(
                        text = number.toString(),
                        fontSize = 80.sp,
                        fontFamily = sevenSegmentFont,
                        color = Color(0xFFFF0000), // Red color typical for seven segment displays
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(4.dp)
                    )
                }
            }
        }
    }
}