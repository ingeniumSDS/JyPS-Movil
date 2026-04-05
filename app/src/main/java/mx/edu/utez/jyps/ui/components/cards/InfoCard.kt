package mx.edu.utez.jyps.ui.components.cards

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mx.edu.utez.jyps.ui.theme.JyPSTheme

/**
 * Basic card for displaying general information.
 */
@Composable
fun InfoCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "ℹ️ Información Importante",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )
            listOf(
                "• Las solicitudes deben ser aprobadas por un admin",
                "• Tu código QR será activado una vez aprobada tu solicitud",
                "• Presenta el QR al personal de seguridad al salir",
                "• Revisa tu historial para ver el estado de tus solicitudes"
            ).forEach {
                Text(
                    text = it,
                    fontSize = 12.sp,
                    color = Color(0xFF4A5565)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun InfoCardPreview() {
    JyPSTheme {
        InfoCard()
    }
}
