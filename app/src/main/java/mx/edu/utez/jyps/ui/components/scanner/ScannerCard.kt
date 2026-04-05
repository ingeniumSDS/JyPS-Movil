package mx.edu.utez.jyps.ui.components.scanner

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
 * Main card wrapping the scanning area and its controls.
 *
 * This component abstracts a large UI block so the screen consumes it without
 * accumulating excessive lines of Compose, favoring maintainability.
 *
 * @param status Current dynamic status of the scan. Determines button states and visible texts.
 * @param modifier Base modifier for scalability with generic Compose layouts.
 */
@Composable
fun ScannerCard(
    status: ScannerStatus,
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Simulated camera scanner visual region. Assuming automatic scanning.
            ScannerBox(isScanning = true)

            // Injected status sub-component (temporary feedback)
            when (status) {
                is ScannerStatus.ValidQR -> StatusText(isValid = true, code = status.code)
                is ScannerStatus.InvalidQR -> StatusText(isValid = false, code = status.error)
                else -> Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Preview
@Composable
fun ScannerCardPreview() {
    ScannerCard(status = ScannerStatus.Idle)
}
