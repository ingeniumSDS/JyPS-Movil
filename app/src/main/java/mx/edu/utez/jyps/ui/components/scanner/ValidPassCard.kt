package mx.edu.utez.jyps.ui.components.scanner

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Success card displayed when the scanned code results in a pass with correct permissions.
 * Displays important identifying information for the security officer operating the scanner.
 *
 * @param name Student / Worker name.
 * @param email Email associated with the account or validation.
 * @param date Readable format of the pass date.
 * @param exitTime The exact time the pass was validated and the user exited.
 * @param returnTime The estimated return time (3 hours limits) or return status.
 * @param code Specific alphanumeric validation code.
 * @param type Type of pass (e.g., "Allow exit").
 * @param onClose Lambda consumed to close the view and reset the scanner state.
 */
@Composable
fun ValidPassCard(
    name: String,
    email: String,
    date: String,
    exitTime: String,
    returnTime: String,
    code: String,
    type: String,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(width = 3.5.dp, color = Color(0xFF28A745), shape = RoundedCornerShape(8.dp)),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Affirmative Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF0FDF4), RoundedCornerShape(8.dp))
                    .padding(vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Válido", color = Color(0xFF28A745), fontSize = 30.sp, fontWeight = FontWeight.Bold)
                Text(type, color = Color(0xFF4A5565), fontSize = 18.sp, fontWeight = FontWeight.Normal)
            }

            // Bearer data
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(name, color = Color(0xFF0F2C59), fontSize = 24.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                Text(email, color = Color(0xFF4A5565), fontSize = 16.sp, fontWeight = FontWeight.Normal)
            }

            // Strict pass operation data
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF9FAFB), RoundedCornerShape(8.dp))
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Fecha de pase", color = Color(0xFF4A5565), fontSize = 14.sp)
                Text(date, color = Color(0xFF0F2C59), fontSize = 18.sp, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(8.dp))

                Text("Hora de salida", color = Color(0xFF4A5565), fontSize = 14.sp)
                Text(exitTime, color = Color(0xFF0F2C59), fontSize = 18.sp, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(8.dp))

                Text("Hora de regreso calculada", color = Color(0xFF4A5565), fontSize = 14.sp)
                Text(returnTime, color = Color(0xFF0F2C59), fontSize = 18.sp, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(16.dp))
                
                Text("Código", color = Color(0xFF4A5565), fontSize = 14.sp)
                Text(
                    text = code,
                    color = Color(0xFF0F2C59),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.2.sp
                )
            }

            // Option to reset / return to default scan environment
            OutlinedButton(
                onClick = onClose,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF0F2C59)),
                border = BorderStroke(1.5.dp, Color(0xFF0F2C59))
            ) {
                Text("Cerrar", fontSize = 16.sp, fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Preview
@Composable
fun ValidPassCardPreview() {
    ValidPassCard(
        name = "Juan Pérez García",
        email = "juan.perez@utez.edu.mx",
        date = "martes, 24 de febrero de 2026",
        exitTime = "09:40 a.m.",
        returnTime = "12:40 p.m.",
        code = "JUST001",
        type = "Permitir Salida",
        onClose = {}
    )
}
