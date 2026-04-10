package mx.edu.utez.jyps.ui.components.admin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mx.edu.utez.jyps.data.model.Usuario
import androidx.compose.ui.tooling.preview.Preview

/**
 * A highly interactive card summarizing a User profile in the admin list.
 * Now utilizes the integrated 'activo' status directly from the [Usuario] model.
 *
 * @param usuario Current serialized user model from API containing profile and status.
 * @param onEditClick Fired to bootstrap edit modifications to this user.
 * @param onToggleStatusClick Fired to change the user's active/inactive state.
 * @param onViewDetail Optional dispatch logic indicating request to open detail overlay.
 */
@Composable
fun UserCard(
    usuario: Usuario,
    onEditClick: (Usuario) -> Unit,
    onToggleStatusClick: (Usuario) -> Unit,
    onViewDetail: ((Long) -> Unit)? = null
) {
    val isActivo = usuario.activo

    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = if (!isActivo) BorderStroke(2.dp, Color(0xFFE5E7EB)) else null
    ) {
        Column(modifier = Modifier.padding(24.dp)) {

            // Header: Avatar, Name & Role
            Row(verticalAlignment = Alignment.Top) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            if (isActivo) Color(0xFF0F2C59) else Color(0xFF99A1AF),
                            RoundedCornerShape(24.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = usuario.initial, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = usuario.nombreCompleto,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isActivo) Color(0xFF0F2C59) else Color(0xFF6A7282)
                    )

                    Spacer(modifier = Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        RolePill(usuario.primaryRoleDisplay)
                        DeptPill(usuario.departamentoDisplay)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ContactRow(icon = Icons.Default.Email, text = usuario.correo, dimmed = !isActivo)
                ContactRow(icon = Icons.Default.Phone, text = usuario.telefono, dimmed = !isActivo)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if (onViewDetail != null) {
                    OutlinedButton(
                        onClick = { onViewDetail(usuario.id) },
                        modifier = Modifier.fillMaxWidth().height(40.dp),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, Color(0xFF6A7282)),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF6A7282))
                    ) {
                        Text("Ver Detalle", fontWeight = FontWeight.Medium, fontSize = 13.sp)
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(
                        onClick = { onEditClick(usuario) },
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.5.dp, Color(0xFF0F2C59)),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF0F2C59)),
                        contentPadding = PaddingValues(horizontal = 8.dp)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Editar", fontWeight = FontWeight.Medium, fontSize = 14.sp, maxLines = 1)
                    }

                    val actionText = if (isActivo) "Desactivar" else "Activar"
                    val actionColor = if (isActivo) Color(0xFFDC3545) else Color(0xFF28A745)
                    val actionIcon = if (isActivo) Icons.Default.Block else Icons.Default.CheckCircleOutline

                    OutlinedButton(
                        onClick = { onToggleStatusClick(usuario) },
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.5.dp, actionColor),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = actionColor),
                        contentPadding = PaddingValues(horizontal = 8.dp)
                    ) {
                        Icon(actionIcon, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(actionText, fontWeight = FontWeight.Medium, fontSize = 14.sp, maxLines = 1)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UserCardPreview() {
    val mockUser = Usuario(id = 1L, nombreCompleto = "Juan Perez Gomez", correo = "juan@utez.edu.mx", telefono = "1234567890", roles = listOf("EMPLEADO"))
    UserCard(usuario = mockUser, onEditClick = {}, onToggleStatusClick = {})
}
