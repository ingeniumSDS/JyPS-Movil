package mx.edu.utez.jyps.ui.components.scanner

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp

/**
 * Displays the result (valid or invalid) of the scanned QR code.
 *
 * Encapsulated here to keep the main screen clean and allow scalability.
 * 
 * @param isValid Indicates if the code was successfully validated.
 * @param code The resulting text to display.
 */
@Composable
fun StatusText(
    isValid: Boolean,
    code: String,
    modifier: Modifier = Modifier
) {
    val text = if (isValid) "✅ Código válido: $code" else "❌ Código inválido: $code"
    
    Text(
        text = text,
        color = Color(0xFF4A5565),
        fontSize = 12.sp,
        fontWeight = FontWeight.Normal,
        textAlign = TextAlign.Center,
        modifier = modifier.fillMaxWidth()
    )
}

@Preview(showBackground = true)
@Composable
fun StatusTextPreview() {
    StatusText(isValid = true, code = "GDKF64NC")
}
