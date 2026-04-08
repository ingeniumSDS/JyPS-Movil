package mx.edu.utez.jyps.ui.components.departmenthead

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import mx.edu.utez.jyps.ui.components.inputs.AppTextField
import mx.edu.utez.jyps.ui.components.inputs.AppDropdown

/**
 * Redesigned CreateEmployeeDialog following Figma mockup.
 * 
 * @param onDismiss Request to close the dialog.
 * @param onConfirm Provides all required employee fields.
 */
@Composable
fun CreateEmployeeDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, email: String, phone: String, empId: String, pos: String, dept: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var employeeId by remember { mutableStateOf("") }
    var position by remember { mutableStateOf("") }
    var department by remember { mutableStateOf("") }
    
    val departments = listOf("DACEA", "DATEFI", "DATID", "DAMI")

    val isFormValid = name.isNotBlank() && email.isNotBlank() && phone.isNotBlank() && department.isNotBlank()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .wrapContentHeight()
                .padding(16.dp),
            shape = RoundedCornerShape(12.dp),
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header with X
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Crear Nuevo Empleado",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F2C59)
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = Color(0xFF6A7282))
                    }
                }

                Divider(color = Color(0xFFF1F3F5))

                // Form Fields (using * for required as in Figma)
                AppTextField(
                    label = "Nombre completo *",
                    value = name,
                    onValueChange = { name = it },
                    placeholder = "Ej. Juan Pérez García"
                )
                AppTextField(
                    label = "Teléfono *",
                    value = phone,
                    onValueChange = { phone = it },
                    placeholder = "777 123 4567"
                )
                AppTextField(
                    label = "Email institucional *",
                    value = email,
                    onValueChange = { email = it },
                    placeholder = "usuario@utez.edu.mx"
                )
                AppDropdown(
                    label = "Departamento *",
                    options = departments,
                    selectedOption = department,
                    onOptionSelected = { department = it }
                )

                // Info Banner (Blue)
                Surface(
                    color = Color(0xFFE7F1FF), // Figma Blue Light
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = Color(0xFF007BFF),
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Información: Se generará una contraseña temporal que será enviada al correo del empleado.",
                            fontSize = 11.sp,
                            color = Color(0xFF0056B3),
                            lineHeight = 16.sp,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Buttons
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0xFF0F2C59))),
                        contentPadding = PaddingValues(12.dp)
                    ) {
                        Text("Cancelar", color = Color(0xFF0F2C59), fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { 
                            onConfirm(name, email, phone, employeeId.ifBlank { "EMP-${(100..999).random()}" }, "Empleado", department) 
                        },
                        enabled = isFormValid,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF28A745)), // Success Green
                        contentPadding = PaddingValues(12.dp)
                    ) {
                        Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Crear Empleado", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
