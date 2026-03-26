package mx.edu.utez.jyps.ui.components.common

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

/**
 * Small floating notification displayed at the bottom of the screen for feedback.
 *
 * @param message The text message to display.
 * @param isVisible Control for showing/hiding the toast with animation.
 * @param onDismiss Callback to clear the message after duration.
 * @param modifier Optional modifier for relative positioning.
 */
@Composable
fun AppToast(
    message: String?,
    isVisible: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(isVisible) {
        if (isVisible) {
            delay(3000L) // Show for 3 seconds
            onDismiss()
        }
    }

    AnimatedVisibility(
        visible = isVisible && message != null,
        enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 24.dp)
                .fillMaxWidth()
                .shadow(elevation = 12.dp, shape = RoundedCornerShape(8.dp))
                .background(Color.White, RoundedCornerShape(8.dp))
                .border(width = 0.5.dp, color = Color.Black.copy(alpha = 0.1f), shape = RoundedCornerShape(8.dp))
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Representative Icon matching Figma mockup
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = Color(0xFF0A0A0A),
                modifier = Modifier.size(20.dp)
            )
            
            Text(
                text = message ?: "",
                color = Color(0xFF0A0A0A),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                lineHeight = 20.sp
            )
        }
    }
}

@Preview(showBackground = false)
@Composable
fun AppToastPreview() {
    Box(Modifier.fillMaxSize()) {
        AppToast(
            message = "Invalid code",
            isVisible = true,
            onDismiss = {},
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}
