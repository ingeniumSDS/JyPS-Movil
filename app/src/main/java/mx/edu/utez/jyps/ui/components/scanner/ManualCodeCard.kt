package mx.edu.utez.jyps.ui.components.scanner

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Card containing the manual entry form for QR codes and testing help info.
 * Designed to match the Figma mockup with precise colors and layout.
 *
 * @param code The current value of the code input.
 * @param onCodeChange Callback for when the code input changes.
 * @param onVerifyClick Callback when the verify button is pressed.
 * @param modifier Optional modifier for layout adjustments.
 */
@Composable
fun ManualCodeCard(
    code: String,
    onCodeChange: (String) -> Unit,
    onVerifyClick: () -> Unit,
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Label and Input field
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Ingresa el código QR",
                    color = Color(0xFF0A0A0A),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                
                OutlinedTextField(
                    value = code,
                    onValueChange = onCodeChange,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(
                            text = "Ejemplo: GDKF64NC",
                            color = Color(0xFF0A0A0A).copy(alpha = 0.5f),
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    },
                    textStyle = TextStyle(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center,
                        letterSpacing = 0.9.sp
                    ),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF0F2C59),
                        unfocusedBorderColor = Color(0xFFD1D5DC)
                    ),
                    singleLine = true
                )
            }

            // Verify Action Button
            Button(
                onClick = onVerifyClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F2C59))
            ) {
                Text(
                    text = "Verificar Código",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            // Blue Help/Test Container
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFEFF6FF), RoundedCornerShape(8.dp))
                    .border(width = 1.dp, color = Color(0xFFBEDBFF), shape = RoundedCornerShape(8.dp))
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Códigos de prueba disponibles:",
                    color = Color(0xFF1C398E),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "✅ GDKF64NC - Código válido (1 solo uso)",
                        color = Color(0xFF193CB8),
                        fontSize = 12.sp
                    )
                    Text(
                        text = "⚠️ LATE - Válido (Fin de jornada)",
                        color = Color(0xFF193CB8),
                        fontSize = 12.sp
                    )
                    Text(
                        text = "❌ EXPIRED - Caducado",
                        color = Color(0xFF193CB8),
                        fontSize = 12.sp
                    )
                    Text(
                        text = "❌ INVALID - Código no válido",
                        color = Color(0xFF193CB8),
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun ManualCodeCardPreview() {
    ManualCodeCard(
        code = "",
        onCodeChange = {},
        onVerifyClick = {}
    )
}
