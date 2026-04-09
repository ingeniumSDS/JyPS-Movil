package mx.edu.utez.jyps.ui.components.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview

/**
 * Displays an error banner for validation or network errors.
 *
 * @param message The error message to display.
 */
@Composable
fun ErrorBanner(message: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFFFE2E2), RoundedCornerShape(8.dp))
            .border(1.dp, Color(0xFFDC3545), RoundedCornerShape(8.dp))
            .padding(12.dp)
    ) {
        Text(message, color = Color(0xFFC10007), fontSize = 14.sp, fontWeight = FontWeight.Medium)
    }
}

@Preview
@Composable
fun ErrorBannerPreview() {
    ErrorBanner("Ocurrió un error al procesar la solicitud.")
}
