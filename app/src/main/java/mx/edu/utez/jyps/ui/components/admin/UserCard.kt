package mx.edu.utez.jyps.ui.components.admin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mx.edu.utez.jyps.data.model.UserWithDetails

@Composable
fun UserCard(
    userWithDetails: UserWithDetails,
    onEditClick: (UserWithDetails) -> Unit,
    onToggleStatusClick: (UserWithDetails) -> Unit
) {
    val initial = userWithDetails.usuario.nombre.firstOrNull()?.toString()?.uppercase() ?: "?"
    val primaryRole = userWithDetails.primaryRole?.nombre ?: "Sin Rol"
    val isActivo = userWithDetails.cuenta.activa

    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            // Header: Avatar, Name & Role/Dept Pills
            Row(verticalAlignment = Alignment.Top) {
                // Avatar
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            if (isActivo) Color(0xFF0F2C59) else Color(0xFF99A1AF),
                            RoundedCornerShape(24.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = initial, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = userWithDetails.usuario.nombreCompleto,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isActivo) Color(0xFF0F2C59) else Color(0xFF6A7282)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        RolePill(primaryRole)
                        
                        if (!isActivo) {
                            StatusPill("Inactiva", Color(0xFFC10007), Color(0xFFFFE2E2))
                        } else {
                            DepartmentPill(userWithDetails.departamento.nombre)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Contact Info
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ContactRow(icon = Icons.Default.Email, text = userWithDetails.usuario.correo)
                ContactRow(icon = Icons.Default.Phone, text = userWithDetails.usuario.telefono)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedButton(
                    onClick = { onEditClick(userWithDetails) },
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.5.dp, Color(0xFF0F2C59)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF0F2C59))
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Editar", fontWeight = FontWeight.Medium)
                }

                val actionText = if (isActivo) "Desactivar" else "Activar"
                val actionColor = if (isActivo) Color(0xFFDC3545) else Color(0xFF28A745)
                val actionIcon = if (isActivo) Icons.Default.Block else Icons.Default.CheckCircleOutline

                OutlinedButton(
                    onClick = { onToggleStatusClick(userWithDetails) },
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.5.dp, actionColor),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = actionColor)
                ) {
                    Icon(actionIcon, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(actionText, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

@Composable
private fun RolePill(role: String) {
    val (bgColor, textColor) = when (role) {
        "Administrador" -> Pair(Color(0xFFFFE2E2), Color(0xFFC10007))
        "Trabajador" -> Pair(Color(0xFFDBEAFE), Color(0xFF1447E6))
        "Jefe de Área" -> Pair(Color(0xFFF3E8FF), Color(0xFF8200DB))
        "Recursos Humanos" -> Pair(Color(0xFFDCFCE7), Color(0xFF008236))
        "Seguridad" -> Pair(Color(0xFFFFEDD4), Color(0xFFCA3500))
        else -> Pair(Color(0xFFF3F4F6), Color(0xFF4A5565))
    }

    Box(
        modifier = Modifier
            .background(bgColor, RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 4.dp)
    ) {
        Text(text = role, color = textColor, fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun DepartmentPill(department: String) {
    Row(
        modifier = Modifier
            .background(Color(0xFFF3F4F6), RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.Business, contentDescription = null, tint = Color(0xFF364153), modifier = Modifier.size(12.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = department, color = Color(0xFF364153), fontSize = 12.sp, fontWeight = FontWeight.Normal)
    }
}

@Composable
private fun StatusPill(status: String, textColor: Color, bgColor: Color) {
    Box(
        modifier = Modifier
            .background(bgColor, RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 4.dp)
    ) {
        Text(text = status, color = textColor, fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun ContactRow(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = Color(0xFF99A1AF), modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text, color = Color(0xFF4A5565), fontSize = 14.sp)
    }
}
