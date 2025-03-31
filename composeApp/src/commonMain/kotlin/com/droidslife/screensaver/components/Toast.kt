package com.droidslife.screensaver.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import kotlinx.coroutines.delay

/**
 * Data class to hold toast state
 */
class ToastState {
    var isVisible by mutableStateOf(false)
    var message by mutableStateOf("")

    fun show(msg: String) {
        message = msg
        isVisible = true
    }

    fun hide() {
        isVisible = false
    }
}

/**
 * Remember a toast state
 */
@Composable
fun rememberToastState(): ToastState {
    return remember { ToastState() }
}

/**
 * A toast component that shows a message with a transparent background.
 * 
 * @param toastState The state of the toast
 * @param modifier Additional modifier for the toast
 */
@Composable
fun ShortcutToast(
    toastState: ToastState,
    modifier: Modifier = Modifier
) {
    if (toastState.isVisible) {
        Popup(
            alignment = Alignment.TopCenter,
            onDismissRequest = { toastState.hide() }
        ) {
            Box(
                modifier = modifier
                    .padding(top = 50.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = toastState.message,
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Gray.copy(alpha = 0.5f))
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            // Auto-dismiss after 1.5 seconds
            LaunchedEffect(toastState.isVisible) {
                if (toastState.isVisible) {
                    delay(1500)
                    toastState.hide()
                }
            }
        }
    }
}
