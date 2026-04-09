package mx.edu.utez.jyps.ui.components.modals

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import mx.edu.utez.jyps.ui.theme.JyPSTheme
import mx.edu.utez.jyps.ui.components.common.BulletText

/**
 * Educational modal displaying application security responsibilities and limits.
 *
 * @param onDismissRequest Triggered request to close the Dialog overlay.
 */
@Composable
fun SecurityNoticeModal(
    onDismissRequest: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        ModalContainer(
            title = "Información de Seguridad",
            onDismissRequest = onDismissRequest
        ) {
            // Warning Box
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFFEF2F2), RoundedCornerShape(8.dp))
                    .padding(start = 4.dp) // Red left border effect
                    .background(Color(0xFFFEF2F2), RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp)),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Simplified red border trick
                Column(modifier = Modifier
                    .background(Color(0xFFFB2C36))
                    .padding(start = 3.dp)
                    .background(Color(0xFFFEF2F2))
                    .padding(12.dp)
                ) {
                    Text(
                        text = "⚠️ Advertencia Importante",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color(0xFF9F0712)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Este sistema NO está diseñado ni autorizado para almacenar información médica, diagnósticos o expedientes clínicos completos. Solo debe usarse para procesar la justificación administrativa de ausencias.",
                        fontSize = 12.sp,
                        color = Color(0xFFC10007),
                        lineHeight = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Restrictions
            Text(
                text = "❌ NO incluya en sus solicitudes:",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            BulletText("Diagnósticos médicos detallados o condiciones de salud")
            BulletText("Información financiera (números de cuenta, tarjetas)")
            BulletText("Documentos oficiales con datos personales (INE, CURP) completos y legibles sin encriptar")
            BulletText("Información de terceros sin su consentimiento")
            BulletText("Datos de menores de edad sin autorización parental")

            Spacer(modifier = Modifier.height(16.dp))

            // Recommendations
            Text(
                text = "✅ Recomendaciones de Uso:",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            BulletText("Use descripciones generales para justificantes (\"Cita médica\", \"Trámite personal\")")
            BulletText("No comparta su sesión activa con otras personas")
            BulletText("Cierre sesión al terminar de usar el sistema en dispositivos compartidos")
            BulletText("Reporte cualquier irregularidad al administrador del sistema")

            Spacer(modifier = Modifier.height(16.dp))

            // Security Measures
            Text(
                text = "🔒 Medidas de Seguridad Actuales:",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            BulletText("Almacenamiento local cifrado en el navegador")
            BulletText("Validación de cuentas activas/inactivas")
            BulletText("Control de acceso basado en roles")
            BulletText("Protección de rutas por autenticación")

            Spacer(modifier = Modifier.height(16.dp))

            // Note Box
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFEFF6FF), RoundedCornerShape(8.dp))
                    .background(Color(0xFF2B7FFF))
                    .padding(start = 3.dp)
                    .background(Color(0xFFEFF6FF), RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp))
                    .padding(12.dp)
            ) {
                Text(
                    text = "Nota: Este es un sistema de demostración educativo desarrollado bajo el entorno de JyPS. La información almacenada es referencial y local.",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = Color(0xFF193CB8),
                    lineHeight = 16.sp
                )
            }
        }
    }
}

@Preview
@Composable
fun SecurityModalPreview() {
    JyPSTheme {
        SecurityNoticeModal(onDismissRequest = {})
    }
}
