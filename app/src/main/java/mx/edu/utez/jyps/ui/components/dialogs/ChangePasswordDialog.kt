package mx.edu.utez.jyps.ui.components.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.Row

/**
 * Reusable layout for changing passwords injected to the AppFormDialog Slot.
 */
@Composable
fun ChangePasswordDialog(
    onDismissRequest: () -> Unit,
    onSave: (current: String, new: String, confirm: String) -> Unit
) {
    var currentPwd by remember { mutableStateOf("") }
    var newPwd by remember { mutableStateOf("") }
    var confirmPwd by remember { mutableStateOf("") }

    var currentPwdVisible by remember { mutableStateOf(false) }
    var newPwdVisible by remember { mutableStateOf(false) }
    var confirmPwdVisible by remember { mutableStateOf(false) }

    val passwordsMatch = newPwd == confirmPwd
    val isFormValid = currentPwd.isNotBlank() && newPwd.isNotBlank() && passwordsMatch && newPwd.length >= 6

    AppFormDialog(
        title = "Cambiar Contraseña",
        onDismissRequest = onDismissRequest,
        onCancel = onDismissRequest,
        onSave = { onSave(currentPwd, newPwd, confirmPwd) },
        isSaveEnabled = isFormValid
    ) {
        // Field 1: Current Password
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row {
                Text("Contraseña Actual ", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color(0xFF364153))
                Text("*", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color.Red)
            }
            OutlinedTextField(
                value = currentPwd,
                onValueChange = { currentPwd = it },
                placeholder = { Text("Ingresa tu contraseña actual", color = Color(0xFF6A7282), fontSize = 14.sp) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = if (currentPwdVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFF6A7282)) },
                trailingIcon = {
                    IconButton(onClick = { currentPwdVisible = !currentPwdVisible }) {
                        Icon(
                            imageVector = if (currentPwdVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = if (currentPwdVisible) "Ocultar Contraseña" else "Mostrar Contraseña",
                            tint = Color(0xFF6A7282)
                        )
                    }
                },
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = Color(0xFFD1D5DC)
                )
            )
            Text(
                text = "Necesaria para confirmar el cambio de contraseña",
                fontSize = 12.sp,
                color = Color(0xFF6A7282),
                modifier = Modifier.padding(top = 2.dp)
            )
        }

        // Field 2: New Password
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row {
                Text("Nueva Contraseña ", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color(0xFF364153))
                Text("*", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color.Red)
            }
            OutlinedTextField(
                value = newPwd,
                onValueChange = { newPwd = it },
                placeholder = { Text("Crea una contraseña segura", color = Color(0xFF6A7282), fontSize = 14.sp) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = if (newPwdVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFF6A7282)) },
                trailingIcon = {
                    IconButton(onClick = { newPwdVisible = !newPwdVisible }) {
                        Icon(
                            imageVector = if (newPwdVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = if (newPwdVisible) "Ocultar Contraseña" else "Mostrar Contraseña",
                            tint = Color(0xFF6A7282)
                        )
                    }
                },
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = Color(0xFFD1D5DC)
                ),
                isError = newPwd.isNotEmpty() && newPwd.length < 6,
                supportingText = {
                    if (newPwd.isNotEmpty() && newPwd.length < 6) {
                        Text("Debe contener al menos 6 caracteres", color = Color.Red)
                    }
                }
            )
        }

        // Field 3: Confirm New Password
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row {
                Text("Confirmar Nueva Contraseña ", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color(0xFF364153))
                Text("*", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color.Red)
            }
            OutlinedTextField(
                value = confirmPwd,
                onValueChange = { confirmPwd = it },
                placeholder = { Text("Repite tu nueva contraseña", color = Color(0xFF6A7282), fontSize = 14.sp) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = if (confirmPwdVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFF6A7282)) },
                trailingIcon = {
                    IconButton(onClick = { confirmPwdVisible = !confirmPwdVisible }) {
                        Icon(
                            imageVector = if (confirmPwdVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = if (confirmPwdVisible) "Ocultar Contraseña" else "Mostrar Contraseña",
                            tint = Color(0xFF6A7282)
                        )
                    }
                },
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = Color(0xFFD1D5DC)
                ),
                isError = !passwordsMatch && confirmPwd.isNotEmpty(),
                supportingText = {
                    if (!passwordsMatch && confirmPwd.isNotEmpty()) {
                        Text("Las contraseñas no coinciden", color = Color.Red)
                    }
                }
            )
        }
    }
}
