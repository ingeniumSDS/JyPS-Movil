package mx.edu.utez.jyps.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import mx.edu.utez.jyps.data.model.Usuario
import mx.edu.utez.jyps.viewmodel.AdminViewModel

@Composable
fun UserDetailDialog(viewModel: AdminViewModel) {
    val isVisible by viewModel.isUserDetailVisible.collectAsStateWithLifecycle()
    val user by viewModel.selectedUser.collectAsStateWithLifecycle()

    if (isVisible && user != null) {
        Dialog(
            onDismissRequest = { viewModel.closeUserDetail() },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .background(Color.White, RoundedCornerShape(8.dp))
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Detalle de Usuario", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF101828))
                        IconButton(onClick = { viewModel.closeUserDetail() }) {
                            Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = Color(0xFF6A7282))
                        }
                    }

                    HorizontalDivider(color = Color(0xFFE5E7EB))
                    user?.let { DetailContent(it) }
                }
            }
        }
    }
}

@Composable
private fun DetailContent(usuario: Usuario) {
    Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {

        // Avatar + Name
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Box(
                modifier = Modifier.size(56.dp).background(Color(0xFF0F2C59), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = usuario.nombre.take(1).uppercase() + usuario.apellidoPaterno.take(1).uppercase(),
                    color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp
                )
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
        DetailRow(Icons.Default.Person, "Departamento", "Depto. ${usuario.departamentoId}")

        // Schedule (only for EMPLEADO)
        if (usuario.roles.contains("EMPLEADO") && usuario.horaEntrada != null) {
            HorizontalDivider(color = Color(0xFFE5E7EB))
            Text("Jornada Laboral", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = Color(0xFF364153))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Entrada", fontSize = 12.sp, color = Color.Gray)
                    Text(usuario.horaEntrada.take(5), fontWeight = FontWeight.Medium, color = Color(0xFF0F2C59))
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("Salida", fontSize = 12.sp, color = Color.Gray)
                    Text(usuario.horaSalida?.take(5) ?: "--:--", fontWeight = FontWeight.Medium, color = Color(0xFF0F2C59))
                }
            }
        }

        // Account status
        usuario.cuenta?.let { cuenta ->
            HorizontalDivider(color = Color(0xFFE5E7EB))
            Text("Estado de la Cuenta", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = Color(0xFF364153))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                val (bg, tc) = if (cuenta.activa) Color(0xFFDCFCE7) to Color(0xFF16A34A) else Color(0xFFFEE2E2) to Color(0xFFDC2626)
                Box(modifier = Modifier.background(bg, RoundedCornerShape(50)).padding(horizontal = 12.dp, vertical = 4.dp)) {
                    Text(if (cuenta.activa) "Activa" else "Inactiva", fontSize = 12.sp, color = tc, fontWeight = FontWeight.Medium)
                }
                if (cuenta.bloqueada) {
                    Box(modifier = Modifier.background(Color(0xFFFEE2E2), RoundedCornerShape(50)).padding(horizontal = 12.dp, vertical = 4.dp)) {
                        Text("Bloqueada", fontSize = 12.sp, color = Color(0xFFDC2626), fontWeight = FontWeight.Medium)
                    }
                }
            }
        }

        Spacer(Modifier.height(8.dp))
    }
}

@Composable
private fun DetailRow(icon: ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Icon(icon, contentDescription = null, tint = Color(0xFF6A7282), modifier = Modifier.size(18.dp))
        Column {
            Text(label, fontSize = 12.sp, color = Color.Gray)
            Text(value, fontSize = 14.sp, color = Color(0xFF101828), fontWeight = FontWeight.Medium)
        }
    }
}

private fun roleDisplayName(role: String) = when (role) {
    "EMPLEADO" -> "Trabajador"; "GUARDIA" -> "Seguridad"
    "JEFE_DE_DEPARTAMENTO" -> "Jefe de Área"; "ADMINISTRADOR" -> "Administrador"
    "AUDITOR" -> "Auditor"; else -> role
}

private fun roleColors(role: String): Pair<Color, Color> = when (role) {
    "ADMINISTRADOR" -> Color(0xFFFEE2E2) to Color(0xFFDC2626)
    "JEFE_DE_DEPARTAMENTO" -> Color(0xFFFFF3CD) to Color(0xFF856404)
    "EMPLEADO" -> Color(0xFFDEEBFF) to Color(0xFF1D4ED8)
    "GUARDIA" -> Color(0xFFE0F2F1) to Color(0xFF00796B)
    "AUDITOR" -> Color(0xFFF3E8FF) to Color(0xFF7C3AED)
    else -> Color(0xFFF3F4F6) to Color(0xFF6B7280)
}
