package mx.edu.utez.jyps.ui.components.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview

/**
 * Static reusable info box component.
 * Mentions that user password creation is fully automated.
 */
@Composable
fun PasswordInfoBox() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFEFF6FF), RoundedCornerShape(8.dp))
            .border(1.dp, Color(0xFFBEDBFF), RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(
                modifier = Modifier.size(40.dp).background(Color(0xFFDBEAFE), RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFF155DFC), modifier = Modifier.size(20.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text("Contraseña Generada Automáticamente", fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color(0xFF1C398E))
                Spacer(Modifier.height(4.dp))
                Text("Al crear el usuario, se generará una contraseña segura automáticamente.", fontSize = 14.sp, color = Color(0xFF1447E6))
                Spacer(Modifier.height(8.dp))
                Text("✓ El usuario recibirá sus credenciales de acceso", fontSize = 12.sp, color = Color(0xFF155DFC))
                Text("✓ Se recomienda cambiar la contraseña en el primer inicio", fontSize = 12.sp, color = Color(0xFF155DFC))
            }
        }
    }
}

@Preview
@Composable
fun PasswordInfoBoxPreview() {
    PasswordInfoBox()
}
