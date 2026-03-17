package mx.edu.utez.jyps.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import mx.edu.utez.jyps.viewmodel.AdminViewModel

@Composable
fun CreateUserDialog(viewModel: AdminViewModel) {
    val isVisible by viewModel.isCreateUserVisible.collectAsStateWithLifecycle()

    if (isVisible) {
        Dialog(
            onDismissRequest = { viewModel.setCreateUserVisible(false) },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Color.Black.copy(alpha = 0.4f)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
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
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Crear Nuevo Usuario",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF101828)
                            )
                            IconButton(onClick = { viewModel.setCreateUserVisible(false) }) {
                                Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = Color(0xFF6A7282))
                            }
                        }

                        HorizontalDivider(color = Color(0xFFE5E7EB))

                        Column(modifier = Modifier.padding(24.dp)) {
                            // Form Fields
                            val name by viewModel.newUserName.collectAsStateWithLifecycle()
                            val paterno by viewModel.newUserPaterno.collectAsStateWithLifecycle()
                            val materno by viewModel.newUserMaterno.collectAsStateWithLifecycle()
                            val phone by viewModel.newUserPhone.collectAsStateWithLifecycle()
                            val email by viewModel.newUserEmail.collectAsStateWithLifecycle()

                            FormField(label = "Nombre *", value = name, onValueChange = viewModel::onNameChange)
                            Spacer(Modifier.height(16.dp))
                            FormField(label = "Apellido Paterno *", value = paterno, onValueChange = viewModel::onPaternoChange)
                            Spacer(Modifier.height(16.dp))
                            FormField(label = "Apellido Materno *", value = materno, onValueChange = viewModel::onMaternoChange)
                            Spacer(Modifier.height(16.dp))
                            FormField(label = "Teléfono *", value = phone, placeholder = "777 123 4567", onValueChange = viewModel::onPhoneChange)
                            Spacer(Modifier.height(16.dp))
                            FormField(label = "Email institucional *", value = email, placeholder = "usuario@utez.edu.mx", onValueChange = viewModel::onEmailChange)
                            
                            Spacer(Modifier.height(24.dp))

                            // Roles Selection
                            Text("Roles * (Seleccione uno o más)", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color(0xFF364153))
                            Spacer(Modifier.height(8.dp))
                            
                            val selectedRoles by viewModel.newUserRoles.collectAsStateWithLifecycle()
                            val roles = listOf(
                                1 to "Trabajador",
                                3 to "Jefe de Área",
                                2 to "Seguridad",
                                5 to "Recursos Humanos",
                                4 to "Administrador"
                            )

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, Color(0xFFD1D5DC), RoundedCornerShape(8.dp))
                                    .padding(8.dp)
                            ) {
                                roles.forEach { (id, label) ->
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { viewModel.toggleRole(id) }
                                            .padding(vertical = 4.dp)
                                    ) {
                                        Checkbox(
                                            checked = selectedRoles.contains(id),
                                            onCheckedChange = { viewModel.toggleRole(id) },
                                            colors = CheckboxDefaults.colors(checkedColor = Color(0xFF0F2C59))
                                        )
                                        Text(label, fontSize = 14.sp, color = Color(0xFF0A0A0A))
                                    }
                                }
                            }
                            
                            if (selectedRoles.isEmpty()) {
                                Text(
                                    "Debe seleccionar al menos un rol",
                                    color = Color(0xFFFB2C36),
                                    fontSize = 14.sp,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                            }

                            Spacer(Modifier.height(24.dp))

                            // Info Box
                            PasswordInfoBox()

                            Spacer(Modifier.height(24.dp))

                            // Actions
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { viewModel.setCreateUserVisible(false) },
                                    modifier = Modifier.weight(1f).height(48.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF0F2C59))
                                ) {
                                    Text("Cancelar", color = Color(0xFF0F2C59))
                                }

                                val isValid = name.isNotBlank() && paterno.isNotBlank() && phone.isNotBlank() && email.isNotBlank() && selectedRoles.isNotEmpty()

                                Button(
                                    onClick = { viewModel.saveNewUser() },
                                    modifier = Modifier.weight(1f).height(48.dp),
                                    enabled = isValid,
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF28A745),
                                        disabledContainerColor = Color(0xFF28A745).copy(alpha = 0.5f)
                                    )
                                ) {
                                    Text("Crear Usuario", color = Color.White)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FormField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = ""
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = label, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color(0xFF364153))
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(placeholder, color = Color.Gray.copy(alpha = 0.5f)) },
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF0F2C59),
                unfocusedBorderColor = Color(0xFFD1D5DC)
            ),
            singleLine = true
        )
    }
}

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
                modifier = Modifier
                    .size(40.dp)
                    .background(Color(0xFFDBEAFE), RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = Color(0xFF155DFC),
                    modifier = Modifier.size(20.dp)
                )
            }
            
            Column {
                Text(
                    text = "Contraseña Generada Automáticamente",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF1C398E)
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Al crear el usuario, se generará una contraseña segura automáticamente.",
                    fontSize = 14.sp,
                    color = Color(0xFF1447E6)
                )
                Spacer(Modifier.height(8.dp))
                Text(text = "✓ El usuario recibirá sus credenciales de acceso", fontSize = 12.sp, color = Color(0xFF155DFC))
                Text(text = "✓ Se recomienda cambiar la contraseña en el primer inicio", fontSize = 12.sp, color = Color(0xFF155DFC))
            }
        }
    }
}
