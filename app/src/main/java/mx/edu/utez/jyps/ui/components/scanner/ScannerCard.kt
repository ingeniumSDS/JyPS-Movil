package mx.edu.utez.jyps.ui.components.scanner

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mx.edu.utez.jyps.viewmodel.ScannerStatus

/**
 * Card that wraps the [ScannerBox] and provides a title and subtitle context header.
 * Passes camera callbacks down to [ScannerBox] to maintain strict state hoisting.
 *
 * @param status Current scanner status — used to determine if frame feedback is active.
 * @param isQrInFrame Whether the analyzer currently detects a QR in frame.
 * @param onQrDetected Callback forwarded to [ScannerBox] → [CameraPreview] → [QRCodeAnalyzer].
 * @param onFrameWithQr Forwarded to [ScannerBox] to toggle the scanning overlay.
 * @param modifier External modifier for layout flexibility.
 */
@Composable
fun ScannerCard(
    status: ScannerStatus,
    isQrInFrame: Boolean = false,
    onQrDetected: (String) -> Unit = {},
    onFrameWithQr: (Boolean) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Escanear Código QR",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F2C59)
            )
            Text(
                text = "Apunta la cámara al código QR del pase del empleado",
                fontSize = 13.sp,
                color = Color(0xFF64748B)
            )

            // Real camera with golden overlay and animated QR indicator
            ScannerBox(
                isQrInFrame = isQrInFrame,
                onQrDetected = onQrDetected,
                onFrameWithQr = onFrameWithQr
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ScannerCardPreview() {
    ScannerCard(status = ScannerStatus.Idle)
}
