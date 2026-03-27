package mx.edu.utez.jyps.ui.components.scanner

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Visual container of the scanner simulating the camera view.
 *
 * Displays a central dark box, a representative icon, and 
 * golden corners indicating the focus zone as marked in Figma.
 * Uses a dark overlay to simulate scanning progress/wait state.
 * 
 * @param isScanning Determines if scanning is in progress, 
 *        displaying "Scanning..." text and changing feedback.
 * @param modifier Base modifier for size and external position adjustments.
 */
@Composable
fun ScannerBox(
    isScanning: Boolean = false,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f) // Square aspect as in mockup
            .background(Color(0xFF101828), RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center
    ) {
        // Central object (Message and icon representing the scanner)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.align(Alignment.Center)
        ) {
            Icon(
                imageVector = Icons.Default.QrCodeScanner,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.5f),
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = if (isScanning) "Escaneando..." else "Presiona para escanear",
                color = Color.White.copy(alpha = if (isScanning) 1f else 0.75f),
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal
            )
        }

        // Golden corners of the scanner viewbox
        Box(
            modifier = Modifier.size(192.dp)
        ) {
            val cornerColor = Color(0xFFD4AF37)
            val stroke = 3.5.dp
            val size = 24.dp

            // Top-Left corner
            Box(modifier = Modifier.align(Alignment.TopStart).size(size)) {
                Box(modifier = Modifier.fillMaxWidth().height(stroke).background(cornerColor).align(Alignment.TopStart))
                Box(modifier = Modifier.fillMaxHeight().width(stroke).background(cornerColor).align(Alignment.TopStart))
            }
            // Top-Right corner
            Box(modifier = Modifier.align(Alignment.TopEnd).size(size)) {
                Box(modifier = Modifier.fillMaxWidth().height(stroke).background(cornerColor).align(Alignment.TopEnd))
                Box(modifier = Modifier.fillMaxHeight().width(stroke).background(cornerColor).align(Alignment.TopEnd))
            }
            // Bottom-Left corner
            Box(modifier = Modifier.align(Alignment.BottomStart).size(size)) {
                Box(modifier = Modifier.fillMaxWidth().height(stroke).background(cornerColor).align(Alignment.BottomStart))
                Box(modifier = Modifier.fillMaxHeight().width(stroke).background(cornerColor).align(Alignment.BottomStart))
            }
            // Bottom-Right corner
            Box(modifier = Modifier.align(Alignment.BottomEnd).size(size)) {
                Box(modifier = Modifier.fillMaxWidth().height(stroke).background(cornerColor).align(Alignment.BottomEnd))
                Box(modifier = Modifier.fillMaxHeight().width(stroke).background(cornerColor).align(Alignment.BottomEnd))
            }
        }
    }
}

@Preview
@Composable
fun ScannerBoxPreview() {
    ScannerBox()
}
