package mx.edu.utez.jyps.ui.components.departmenthead

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
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
import mx.edu.utez.jyps.ui.components.admin.TimeFieldWithPicker
import androidx.compose.ui.tooling.preview.Preview

/**
 * Redesigned CreateEmployeeDialog following Figma mockup.
 * Adds Entry/Exit clock pickers for departmental personnel management.
 * 
 * @param onDismiss Request to close the dialog.
 * @param onConfirm Provides all required employee fields for institutional registration.
 */
@Composable
fun CreateEmployeeDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, email: String, phone: String, entryTime: String, exitTime: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    
    // Default working hours: 08:00 - 16:00
    var entryHour by remember { mutableIntStateOf(8) }
    var entryMinute by remember { mutableIntStateOf(0) }
    var exitHour by remember { mutableIntStateOf(16) }
    var exitMinute by remember { mutableIntStateOf(0) }
    
    val isFormValid = name.isNotBlank() && email.isNotBlank() && phone.length >= 10

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
                        text = "Registrar Nuevo Empleado",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F2C59)
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = Color(0xFF6A7282))
                    }
                }

                Divider(color = Color(0xFFF1F3F5))

                // Basic Identity Fields
                AppTextField(
                    label = "Nombre completo *",
                    value = name,
                    onValueChange = { name = it },
                    placeholder = "Ej. Juan Pérez García"
                )
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(modifier = Modifier.weight(1f)) {
                        AppTextField(
                            label = "Teléfono *",
                            value = phone,
                            onValueChange = { if (it.length <= 10) phone = it },
                            placeholder = "7771234567"
                        )
                    }
                    Box(modifier = Modifier.weight(1.2f)) {
                        AppTextField(
                            label = "Email institucional *",
                            value = email,
                            onValueChange = { email = it },
                            placeholder = "usuario@utez.edu.mx"
                        )
                    }
                }

                Text(
                    text = "Horario de Jornada Laboral",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F2C59),
                    modifier = Modifier.padding(top = 8.dp)
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(modifier = Modifier.weight(1f)) {
                        TimeFieldWithPicker(
                            label = "Hora Entrada *",
                            hour = entryHour,
                            minute = entryMinute,
                            onTimeSelected = { h, m -> entryHour = h; entryMinute = m }
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        TimeFieldWithPicker(
                            label = "Hora Salida *",
                            hour = exitHour,
                            minute = exitMinute,
                            onTimeSelected = { h, m -> exitHour = h; exitMinute = m }
                        )
                    }
                }

                // Info Banner (Blue)
                Surface(
                    color = Color(0xFFE7F1FF),
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
                            text = "Nota: El empleado será asignado automáticamente a tu departamento. Se le enviará una contraseña temporal por correo.",
                            fontSize = 11.sp,
                            color = Color(0xFF0056B3),
                            lineHeight = 16.sp,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Action Buttons
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        onClick = { 
                            val entry = "%02d:%02d:00".format(entryHour, entryMinute)
                            val exit = "%02d:%02d:00".format(exitHour, exitMinute)
                            onConfirm(name, email, phone, entry, exit) 
                        },
                        enabled = isFormValid,
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF28A745)),
                        contentPadding = PaddingValues(12.dp)
                    ) {
                        Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(20.dp), tint = Color.White)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Crear Empleado", color = Color.White, fontWeight = FontWeight.Bold)
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
}
