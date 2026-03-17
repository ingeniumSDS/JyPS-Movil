package mx.edu.utez.jyps.ui.components.modals

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import mx.edu.utez.jyps.ui.theme.JyPSTheme

@Composable
fun PrivacyPolicyModal(
    onDismissRequest: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        ModalContainer(
            title = "Aviso de Privacidad",
            onDismissRequest = onDismissRequest
        ) {
            Text(
                text = "1. Responsable del Tratamiento de Datos",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "La Universidad Tecnológica Emiliano Zapata del Estado de Morelos (UTEZ) es la responsable del tratamiento de los datos personales que nos proporcione, los cuales serán protegidos conforme a la Ley de Protección de Datos Personales.",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "2. Datos Recopilados",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "El sistema recopila únicamente:",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            BulletText("Nombre completo", 14.sp)
            BulletText("Correo electrónico institucional", 14.sp)
            BulletText("Número de teléfono", 14.sp)
            BulletText("Rol en la institución", 14.sp)
            BulletText("Información sobre solicitudes de pases y justificantes", 14.sp)

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "3. Finalidad del Tratamiento",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Sus datos serán utilizados exclusivamente para:",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            BulletText("Gestionar solicitudes de pases de salida", 14.sp)
            BulletText("Gestionar justificantes de ausencia", 14.sp)
            BulletText("Control de acceso y seguridad institucional", 14.sp)
            BulletText("Reportes estadísticos internos", 14.sp)

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "4. Almacenamiento y Seguridad",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Los datos se almacenan localmente en su navegador para optimizar el rendimiento y la experiencia del usuario.",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "5. Derechos ARCO",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Usted tiene derecho a Acceder, Rectificar, Cancelar y Oponerse (Derechos ARCO) al tratamiento de sus datos personales.",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "6. Contacto",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Para ejercer sus derechos ARCO o cualquier duda sobre este aviso de privacidad, contacte al administrador del sistema.",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Última actualización: 11 de febrero de 2026",
                fontStyle = FontStyle.Italic,
                fontSize = 12.sp,
                color = Color(0xFF6A7282),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            )
        }
    }
}

@Preview
@Composable
fun PrivacyModalPreview() {
    JyPSTheme {
        PrivacyPolicyModal(onDismissRequest = {})
    }
}
