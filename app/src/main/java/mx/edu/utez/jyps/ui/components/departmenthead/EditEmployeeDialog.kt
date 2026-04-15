package mx.edu.utez.jyps.ui.components.departmenthead

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Warning
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
import mx.edu.utez.jyps.data.model.EmployeeItem
import mx.edu.utez.jyps.data.model.DepartamentoResponse
import mx.edu.utez.jyps.ui.components.inputs.AppTextField
import mx.edu.utez.jyps.ui.components.admin.DepartmentDropdown
import mx.edu.utez.jyps.ui.components.admin.TimeFieldWithPicker
import androidx.compose.ui.tooling.preview.Preview

/**
 * Enhanced EditEmployeeDialog with Institutional Data Sync and Transfer Confirmation.
 * 
 * @param employee Initial state of the target member.
 * @param departments Available organizational units for re-assignment.
 * @param onDismiss Request to abort modification.
 * @param onConfirm Provides updated metadata, working hours and deployment target.
 */
@Composable
fun EditEmployeeDialog(
    employee: EmployeeItem,
    departments: List<DepartamentoResponse>,
    onDismiss: () -> Unit,
    onConfirm: (EmployeeItem, entryTime: String, exitTime: String, targetDeptId: Long) -> Unit
) {
    var name by remember { mutableStateOf(employee.fullName) }
    var email by remember { mutableStateOf(employee.email) }
    var phone by remember { mutableStateOf(employee.phone) }
    
    // Track original department to detect transfers
    val originalDeptId = departments.find { it.nombre == employee.department }?.id ?: 0L
    var selectedDeptId by remember { mutableStateOf(originalDeptId) }
    
    // Working hours (Defaults common business hours)
    var entryHour by remember { mutableIntStateOf(8) }
    var entryMinute by remember { mutableIntStateOf(0) }
    var exitHour by remember { mutableIntStateOf(16) }
    var exitMinute by remember { mutableIntStateOf(0) }
    
    var showConfirmTransfer by remember { mutableStateOf(false) }
    
    val isFormValid = name.isNotBlank() && email.isNotBlank() && phone.isNotBlank()

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
                        text = "Actualizar Empleado",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F2C59)
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = Color(0xFF6A7282))
                    }
                }

                Divider(color = Color(0xFFF1F3F5))

                // Profile Section
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    AppTextField(
                        label = "Nombre completo *",
                        value = name,
                        onValueChange = { name = it }
                    )
                    
                    AppTextField(
                        label = "Teléfono *",
                        value = phone,
                        onValueChange = { if (it.length <= 10) phone = it },
                        placeholder = "7771234567"
                    )
                    
                    AppTextField(
                        label = "Correo electrónico *",
                        value = email,
                        onValueChange = { email = it }
                    )
                    
                    DepartmentDropdown(
                        label = "Reasignar Departamento (Opcional)",
                        departamentos = departments,
                        selectedId = selectedDeptId,
                        onSelect = { selectedDeptId = it }
                    )
                }

                Text(
                    text = "Horario de Trabajo",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F2C59)
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(modifier = Modifier.weight(1f)) {
                        TimeFieldWithPicker(
                            label = "Hora Entrada",
                            hour = entryHour,
                            minute = entryMinute,
                            onTimeSelected = { h, m -> entryHour = h; entryMinute = m }
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        TimeFieldWithPicker(
                            label = "Hora Salida",
                            hour = exitHour,
                            minute = exitMinute,
                            onTimeSelected = { h, m -> exitHour = h; exitMinute = m }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Buttons
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        onClick = { 
                            if (selectedDeptId != originalDeptId && selectedDeptId != 0L) {
                                showConfirmTransfer = true
                            } else {
                                val entry = "%02d:%02d:00".format(entryHour, entryMinute)
                                val exit = "%02d:%02d:00".format(exitHour, exitMinute)
                                onConfirm(employee.copy(fullName = name, email = email, phone = phone), entry, exit, selectedDeptId)
                            }
                        },
                        enabled = isFormValid,
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF28A745))
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Guardar Cambios", color = Color.White, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(8.dp),
                        border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0xFF0F2C59))),
                        contentPadding = PaddingValues(12.dp)
                    ) {
                        Text("Cancelar", color = Color(0xFF0F2C59), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    // Secondary Confirmation for Transfer
    if (showConfirmTransfer) {
        val targetDeptName = departments.find { it.id == selectedDeptId }?.nombre ?: "otro departamento"
        AlertDialog(
            onDismissRequest = { showConfirmTransfer = false },
            icon = { Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFB45309)) },
            title = { Text("Confirmar Transferencia") },
            text = { 
                Text("¿Estás seguro de transferir a ${employee.fullName} al departamento de $targetDeptName? Dejará de aparecer en tu dashboard.") 
            },
            confirmButton = {
                Button(
                    onClick = {
                        val entry = "%02d:%02d:00".format(entryHour, entryMinute)
                        val exit = "%02d:%02d:00".format(exitHour, exitMinute)
                        onConfirm(employee.copy(fullName = name, email = email, phone = phone), entry, exit, selectedDeptId)
                        showConfirmTransfer = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB45309))
                ) { Text("Confirmar Transferencia") }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmTransfer = false }) { Text("Cancelar") }
            },
            containerColor = Color.White
        )
    }
}
