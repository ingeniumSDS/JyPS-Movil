package mx.edu.utez.jyps.ui.components.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import mx.edu.utez.jyps.data.model.LinkedUser
import mx.edu.utez.jyps.ui.components.admin.DialogHeader
import mx.edu.utez.jyps.ui.components.buttons.PrimaryButton
import mx.edu.utez.jyps.ui.theme.JyPSTheme

/**
 * Blocking warning dialog shown when attempting to deactivate a department with active employees.
 *
 * @param departmentName The name of the department in conflict.
 * @param linkedUsers List of users that must be reassigned first.
 * @param onClose Callback to dismiss the dialog.
 * @param onManageEmployees Callback to navigate to user management module.
 */
@Composable
fun DeactivateWarningDialog(
    departmentName: String,
    linkedUsers: List<LinkedUser>,
    onClose: () -> Unit,
    onManageEmployees: () -> Unit
) {
    Dialog(onDismissRequest = onClose) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color.White,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                DialogHeader(title = "No se puede desactivar\nel departamento", onClose = onClose)

                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFFFF9E7), RoundedCornerShape(12.dp))
                            .border(1.dp, Color(0xFFFDE68A), RoundedCornerShape(12.dp))
                            .padding(16.dp)
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Icon(Icons.Default.Block, contentDescription = null, tint = Color(0xFFE67E22), modifier = Modifier.size(20.dp))
                            Column {
                                Text(
                                    text = "Usuarios activos vinculados",
                                    color = Color(0xFF92400E),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "Este departamento tiene ${linkedUsers.size} usuario(s) activo(s) vinculado(s). Debes reasignarlos a otro departamento o inactivar sus cuentas antes de poder desactivar este departamento.",
                                    color = Color(0xFFB45309),
                                    fontSize = 13.sp,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }

                    Text("Usuarios vinculados:", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = Color(0xFF0F2C59))

                    linkedUsers.forEach { user ->
                        LinkedUserItem(user)
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFEBF5FB), RoundedCornerShape(12.dp))
                            .border(1.dp, Color(0xFFAED6F1), RoundedCornerShape(12.dp))
                            .padding(16.dp)
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFF2E86C1), modifier = Modifier.size(20.dp))
                            Column {
                                Text("Instrucciones", color = Color(0xFF1B4F72), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Ve a Gestión de Usuarios",
                                    color = Color(0xFF2E86C1),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    modifier = Modifier.clickable { onManageEmployees() }
                                )
                                Text("Reasigna a los usuarios a otro departamento activo, o", fontSize = 13.sp, color = Color(0xFF1B4F72))
                                Text("Inactiva las cuentas de los usuarios vinculados", fontSize = 13.sp, color = Color(0xFF1B4F72))
                                Text("Luego podrás desactivar este departamento", fontSize = 13.sp, color = Color(0xFF1B4F72))
                            }
                        }
                    }

                    PrimaryButton(
                        text = "Entendido",
                        onClick = onClose,
                        backgroundColor = Color(0xFF0F2C59),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DeactivateWarningDialogPreview() {
    JyPSTheme {
        DeactivateWarningDialog(
            departmentName = "Tecnologías de la Información",
            linkedUsers = listOf(LinkedUser(1, "Juan Pérez García", "Trabajador")),
            onClose = {},
            onManageEmployees = {}
        )
    }
}
