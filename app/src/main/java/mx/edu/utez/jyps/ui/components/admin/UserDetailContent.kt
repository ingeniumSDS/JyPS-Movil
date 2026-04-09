package mx.edu.utez.jyps.ui.components.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mx.edu.utez.jyps.data.model.CuentaResponse
import mx.edu.utez.jyps.data.model.Usuario

/**
 * Internal rendering block for user information.
 *
 * @param usuario Target user model.
 * @param cuenta Active account state metadata.
 */
@Composable
fun UserDetailContent(usuario: Usuario, cuenta: CuentaResponse?) {
    Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {

        // Avatar + Name
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Box(
                modifier = Modifier.size(56.dp).background(Color(0xFF0F2C59), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(usuario.initial, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            }
            Column {
                Text(usuario.nombreCompleto, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF101828))
                Text("ID: ${usuario.id}", fontSize = 12.sp, color = Color.Gray)
            }
        }

        // Role chips
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            usuario.roles.forEach { role ->
                val (bg, tc) = roleColors(role)
                Box(modifier = Modifier.background(bg, RoundedCornerShape(50)).padding(horizontal = 12.dp, vertical = 4.dp)) {
                    Text(roleDisplayName(role), fontSize = 12.sp, color = tc, fontWeight = FontWeight.Medium)
                }
            }
        }

        HorizontalDivider(color = Color(0xFFE5E7EB))

        DetailRow(Icons.Default.Email, "Correo", usuario.correo)
        DetailRow(Icons.Default.Phone, "Teléfono", usuario.telefono)
        DetailRow(Icons.Default.Person, "Departamento", usuario.departamentoDisplay)

        // Schedule
        if (usuario.horaEntrada != null) {
            HorizontalDivider(color = Color(0xFFE5E7EB))
            Text("Jornada Laboral", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = Color(0xFF364153))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Entrada", fontSize = 12.sp, color = Color.Gray)
                    Text(usuario.horaEntradaDisplay, fontWeight = FontWeight.Medium, color = Color(0xFF0F2C59))
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("Salida", fontSize = 12.sp, color = Color.Gray)
                    Text(usuario.horaSalidaDisplay, fontWeight = FontWeight.Medium, color = Color(0xFF0F2C59))
                }
            }
        }

        // Account status
        cuenta?.let { c ->
            HorizontalDivider(color = Color(0xFFE5E7EB))
            Text("Estado de la Cuenta", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = Color(0xFF364153))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                val (bg, tc) = if (c.activa) Color(0xFFDCFCE7) to Color(0xFF16A34A) else Color(0xFFFEE2E2) to Color(0xFFDC2626)
                Box(modifier = Modifier.background(bg, RoundedCornerShape(50)).padding(horizontal = 12.dp, vertical = 4.dp)) {
                    Text(if (c.activa) "Activa" else "Inactiva", fontSize = 12.sp, color = tc, fontWeight = FontWeight.Medium)
                }
                if (c.bloqueada) {
                    Box(modifier = Modifier.background(Color(0xFFFEE2E2), RoundedCornerShape(50)).padding(horizontal = 12.dp, vertical = 4.dp)) {
                        Text("Bloqueada", fontSize = 12.sp, color = Color(0xFFDC2626), fontWeight = FontWeight.Medium)
                    }
                }
            }
            if (c.intentosFallidos > 0) {
                Text("Intentos fallidos: ${c.intentosFallidos}", fontSize = 12.sp, color = Color(0xFF6A7282))
            }
        }

        Spacer(Modifier.height(8.dp))
    }
}

/** Maps backend constraints roles to UI strings strings text attributes logic constraint text validation bounds string sequences contexts string definition string definitions arrays parameter strings text bounds variables attributes contexts mappings arrays contexts constraints boolean definitions mapping bounding. */
internal fun roleDisplayName(role: String) = when (role) {
    "EMPLEADO" -> "Empleado"; "GUARDIA" -> "Guardia"
    "JEFE_DE_DEPARTAMENTO" -> "Jefe de Departamento"; "ADMINISTRADOR" -> "Administrador"
    "AUDITOR" -> "Auditor"; else -> role
}

/** Maps target definitions logic arrays string parameter limits textual variable explicitly mapped bounds array context mapping. */
internal fun roleColors(role: String): Pair<Color, Color> = when (role) {
    "ADMINISTRADOR" -> Color(0xFFFEE2E2) to Color(0xFFDC2626)
    "JEFE_DE_DEPARTAMENTO" -> Color(0xFFF3E8FF) to Color(0xFF8200DB)
    "EMPLEADO" -> Color(0xFFDEEBFF) to Color(0xFF1D4ED8)
    "GUARDIA" -> Color(0xFFFFEDD4) to Color(0xFFCA3500)
    "AUDITOR" -> Color(0xFFDCFCE7) to Color(0xFF008236)
    else -> Color(0xFFF3F4F6) to Color(0xFF6B7280)
}


@Preview(showBackground = true)
@Composable
fun UserDetailContentPreview() {
    // Requires a mock user instance
}
