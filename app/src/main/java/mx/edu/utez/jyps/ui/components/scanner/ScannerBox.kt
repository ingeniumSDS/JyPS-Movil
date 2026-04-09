package mx.edu.utez.jyps.ui.components.scanner

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex

/**
 * Visual container for the scanner that renders:
 * 1. The live [CameraPreview] cropped at square 1:1 from the center of the sensor.
 * 2. A decorative overlay of golden corner brackets drawn on top via [Box] + [zIndex].
 * 3. An animated "Escaneando..." overlay that only appears while a QR is in frame.
 *
 * The camera fallback (permission denied) is handled internally by [CameraPreview].
 *
 * @param isQrInFrame Whether the analyzer currently sees a QR code in the camera frame.
 *                    Controls the visibility of the scanning overlay.
 * @param onQrDetected Callback forwarded to [CameraPreview] when a code is decoded.
 * @param onFrameWithQr Forwarded to [CameraPreview] to update [isQrInFrame] in the ViewModel.
 * @param modifier Base modifier for external sizing.
 */
@Composable
fun ScannerBox(
    isQrInFrame: Boolean = false,
    onQrDetected: (String) -> Unit = {},
    onFrameWithQr: (Boolean) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f) // Enforces a 1:1 square frame matching the mockup
            .clip(RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center
    ) {
        // Layer 1: Live camera preview (fills the square via FILL_CENTER crop)
        CameraPreview(
            onQrDetected = onQrDetected,
            onFrameWithQr = onFrameWithQr,
            modifier = Modifier.matchParentSize()
        )

        // Layer 2: Golden corner brackets — always visible over the camera feed
        GoldenCornerOverlay(
            modifier = Modifier
                .size(192.dp)
                .zIndex(1f)
        )

        // Layer 3: "Escaneando..." overlay — only visible when a QR is detected in frame
        AnimatedVisibility(
            visible = isQrInFrame,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.Center)
                .zIndex(2f)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .background(Color.Black.copy(alpha = 0.55f), RoundedCornerShape(8.dp))
            ) {
                Icon(
                    imageVector = Icons.Default.QrCodeScanner,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Escaneando...",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ScannerBoxPreview() {
    ScannerBox(isQrInFrame = false)
}
