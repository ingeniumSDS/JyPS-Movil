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
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateUserDialog(viewModel: AdminViewModel) {
    val isVisible by viewModel.isCreateUserVisible.collectAsStateWithLifecycle()

    if (isVisible) {
        Dialog(
            onDismissRequest = { viewModel.setCreateUserVisible(false) },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.4f)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 8.dp, vertical = 21.dp)
                        .background(Color.White, RoundedCornerShape(8.dp))
                        .fillMaxWidth()
                ) {
                    // Header
                    DialogHeader(title = "Crear Nuevo Usuario", onClose = { viewModel.setCreateUserVisible(false) })

                    val scrollState = rememberScrollState()
                    val scrollToTopTrigger by viewModel.scrollToTopTrigger.collectAsStateWithLifecycle()

                    androidx.compose.runtime.LaunchedEffect(scrollToTopTrigger) {
                        if (scrollToTopTrigger > 0) {
                            scrollState.animateScrollTo(0)
                        }
                    }

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(scrollState)
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        val name by viewModel.newUserName.collectAsStateWithLifecycle()
                        val paterno by viewModel.newUserPaterno.collectAsStateWithLifecycle()
                        val materno by viewModel.newUserMaterno.collectAsStateWithLifecycle()
                        val phone by viewModel.newUserPhone.collectAsStateWithLifecycle()
                        val email by viewModel.newUserEmail.collectAsStateWithLifecycle()
                        val formErrors by viewModel.createFormErrors.collectAsStateWithLifecycle()
                        val serverError by viewModel.createServerResponseError.collectAsStateWithLifecycle()

                        // Error banner
                        serverError?.let { ErrorBanner(it) }

                        // Name fields — stacked vertically for mobile
                        FormField(label = "Nombre *", value = name, onValueChange = viewModel::onNameChange, errorMessage = formErrors["nombre"])
                        FormField(label = "Apellido Paterno *", value = paterno, onValueChange = viewModel::onPaternoChange, errorMessage = formErrors["paterno"])
                        FormField(label = "Apellido Materno *", value = materno, onValueChange = viewModel::onMaternoChange, errorMessage = formErrors["materno"])
                        FormField(label = "Teléfono *", value = phone, placeholder = "777 123 4567", onValueChange = viewModel::onPhoneChange, errorMessage = formErrors["telefono"])
                        FormField(label = "Email institucional *", value = email, placeholder = "usuario@utez.edu.mx", onValueChange = viewModel::onEmailChange, errorMessage = formErrors["email"])

                        // Roles
                        val selectedRoles by viewModel.newUserRoles.collectAsStateWithLifecycle()
                        RolesSelector(selectedRoles = selectedRoles, onToggleRole = viewModel::toggleRole, errorMessage = formErrors["roles"])

                        // Conditional: Department dropdown
                        val showDepartment = selectedRoles.contains(1) || selectedRoles.contains(3)
                        if (showDepartment) {
                            val departamentos by viewModel.departamentos.collectAsStateWithLifecycle()
                            val selectedDeptId by viewModel.newUserDepartmentId.collectAsStateWithLifecycle()
                            DepartmentDropdown(
                                departamentos = departamentos,
                                selectedId = selectedDeptId,
                                onSelect = viewModel::onDepartmentChange,
                                onExpand = viewModel::loadDepartamentos
                            )
                        }

                        // Conditional: Schedule — stacked vertically
                        val showSchedule = selectedRoles.contains(1)
                        if (showSchedule) {
                            val startH by viewModel.newUserStartHour.collectAsStateWithLifecycle()
                            val startM by viewModel.newUserStartMinute.collectAsStateWithLifecycle()
                            val endH by viewModel.newUserEndHour.collectAsStateWithLifecycle()
                            val endM by viewModel.newUserEndMinute.collectAsStateWithLifecycle()

                            TimeFieldWithPicker(label = "Hora Inicio Jornada *", hour = startH, minute = startM, onTimeSelected = viewModel::onStartTimeChange)
                            TimeFieldWithPicker(label = "Hora Fin Jornada *", hour = endH, minute = endM, onTimeSelected = viewModel::onEndTimeChange)
                        }

                        PasswordInfoBox()

                        // Actions: 2 buttons
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedButton(
                                onClick = { viewModel.setCreateUserVisible(false) },
                                modifier = Modifier.weight(1f).height(48.dp),
                                shape = RoundedCornerShape(8.dp),
                                border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF0F2C59))
                            ) { Text("Cancelar", color = Color(0xFF0F2C59), fontWeight = FontWeight.Medium) }

                            Button(
                                onClick = { viewModel.saveNewUser() },
                                modifier = Modifier.weight(1f).height(48.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF28A745))
                            ) { Text("Crear Usuario", color = Color.White, fontWeight = FontWeight.Medium) }
                        }
                    }
                }
            }
        }
    }
}

// ── Shared UI Components ─────────────────────────────

@Composable
fun DialogHeader(title: String, onClose: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF101828))
        IconButton(onClick = onClose, modifier = Modifier.size(28.dp)) {
            Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = Color(0xFF6A7282))
        }
    }
    HorizontalDivider(color = Color(0xFFE5E7EB))
}

@Composable
fun ErrorBanner(message: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFFFE2E2), RoundedCornerShape(8.dp))
            .border(1.dp, Color(0xFFDC3545), RoundedCornerShape(8.dp))
            .padding(12.dp)
    ) {
        Text(message, color = Color(0xFFC10007), fontSize = 14.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun FormField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "",
    errorMessage: String? = null
) {
    val isError = errorMessage != null
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = label, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color(0xFF364153))
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth().height(50.dp),
            placeholder = { Text(placeholder, color = Color(0x800A0A0A), fontSize = 16.sp) },
            shape = RoundedCornerShape(8.dp),
            isError = isError,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF0F2C59),
                unfocusedBorderColor = if (isError) Color(0xFFDC3545) else Color(0xFFD1D5DC),
                errorBorderColor = Color(0xFFDC3545)
            ),
            singleLine = true
        )
        if (isError) {
            Text(
                text = errorMessage!!,
                color = Color(0xFFDC3545),
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 4.dp, start = 8.dp)
            )
        }
    }
}

@Composable
fun RolesSelector(
    selectedRoles: Set<Int>,
    onToggleRole: (Int) -> Unit,
    errorMessage: String? = null
) {
    val hasError = errorMessage != null
    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Roles * (Seleccione uno o más)", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color(0xFF364153))
        Spacer(Modifier.height(8.dp))

        val roles = listOf(
            1 to "Empleado",
            3 to "Jefe de Departamento",
            2 to "Guardia",
            5 to "Auditor",
            4 to "Administrador"
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, if (hasError) Color(0xFFDC3545) else Color(0xFFD1D5DC), RoundedCornerShape(8.dp))
                .padding(horizontal = 16.dp, vertical = 1.dp)
        ) {
            roles.forEach { (id, label) ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onToggleRole(id) }
                        .padding(vertical = 4.dp)
                ) {
                    Checkbox(
                        checked = selectedRoles.contains(id),
                        onCheckedChange = { onToggleRole(id) },
                        colors = CheckboxDefaults.colors(checkedColor = Color(0xFF0F2C59))
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(label, fontSize = 14.sp, color = Color(0xFF0A0A0A))
                }
            }
        }
        if (hasError) {
            Text(
                text = errorMessage!!,
                color = Color(0xFFDC3545),
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 4.dp, start = 8.dp)
            )
        }
    }
}

@Composable
fun DepartmentDropdown(
    departamentos: List<mx.edu.utez.jyps.data.model.Departamento>,
    selectedId: Int,
    onSelect: (Int) -> Unit,
    onExpand: () -> Unit = {}
) {
    var expanded by remember { mutableStateOf(false) }

    androidx.compose.runtime.LaunchedEffect(expanded) {
        if (expanded && departamentos.isEmpty()) {
            onExpand()
        }
    }

    val items = if (departamentos.isNotEmpty()) {
        departamentos.map { it.id.toInt() to it.nombre }
    } else {
        listOf(1 to "Cargando...")
    }

    val selectedName = items.find { it.first == selectedId }?.second ?: "Seleccionar departamento"

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Business, contentDescription = null, tint = Color(0xFF364153), modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(4.dp))
            Text("Departamento *", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color(0xFF364153))
        }
        Spacer(Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true }
        ) {
            OutlinedTextField(
                value = selectedName,
                onValueChange = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .clickable { expanded = true },
                readOnly = true,
                enabled = false, // Use disabled but with custom colors to make it feel like a clickable select
                shape = RoundedCornerShape(8.dp),
                trailingIcon = {
                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Expandir", tint = Color(0xFF364153))
                },
                colors = OutlinedTextFieldDefaults.colors(
                    disabledBorderColor = Color(0xFFD1D5DC),
                    disabledTextColor = Color(0xFF0A0A0A),
                    disabledContainerColor = Color.Transparent,
                    disabledTrailingIconColor = Color(0xFF364153),
                    disabledLabelColor = Color(0xFF364153)
                ),
                singleLine = true
            )

            // Transparent overlay to catch clicks as OutlinedTextField might swallow them even if readOnly
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clickable { expanded = true }
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth(0.9f)
            ) {
                items.forEach { (id, name) ->
                    DropdownMenuItem(
                        text = { Text(name) },
                        onClick = {
                            onSelect(id)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeFieldWithPicker(
    label: String,
    hour: Int, minute: Int,
    onTimeSelected: (Int, Int) -> Unit
) {
    var showPicker by remember { mutableStateOf(false) }
    val h = if (hour == 0) 12 else if (hour > 12) hour - 12 else hour
    val amPm = if (hour < 12) "AM" else "PM"
    val displayText = "%d:%02d %s".format(h, minute, amPm)

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.AccessTime, contentDescription = null, tint = Color(0xFF364153), modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(4.dp))
            Text(text = label, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color(0xFF364153))
        }
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = displayText,
            onValueChange = {},
            modifier = Modifier.fillMaxWidth().height(50.dp),
            readOnly = true,
            shape = RoundedCornerShape(8.dp),
            trailingIcon = {
                IconButton(onClick = { showPicker = true }) {
                    Icon(Icons.Default.AccessTime, contentDescription = "Seleccionar hora", tint = Color(0xFF364153))
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF0F2C59),
                unfocusedBorderColor = Color(0xFFD1D5DC)
            ),
            singleLine = true
        )
    }

    if (showPicker) {
        val timePickerState = rememberTimePickerState(initialHour = hour, initialMinute = minute, is24Hour = false)
        Dialog(onDismissRequest = { showPicker = false }) {
            Surface(shape = RoundedCornerShape(16.dp), color = Color.White) {
                Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(label.replace(" *", ""), fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF0F2C59))
                    Spacer(Modifier.height(16.dp))
                    TimePicker(
                        state = timePickerState,
                        colors = TimePickerDefaults.colors(
                            clockDialColor = Color(0xFFF3F4F6),
                            selectorColor = Color(0xFF0F2C59),
                            containerColor = Color.White,
                            clockDialSelectedContentColor = Color.White,
                            clockDialUnselectedContentColor = Color(0xFF364153),
                            timeSelectorSelectedContainerColor = Color(0xFF0F2C59).copy(alpha = 0.1f),
                            timeSelectorUnselectedContainerColor = Color(0xFFF3F4F6),
                            timeSelectorSelectedContentColor = Color(0xFF0F2C59),
                            timeSelectorUnselectedContentColor = Color(0xFF364153)
                        )
                    )
                    Spacer(Modifier.height(16.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedButton(onClick = { showPicker = false }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(8.dp)) {
                            Text("Cancelar")
                        }
                        Button(
                            onClick = { onTimeSelected(timePickerState.hour, timePickerState.minute); showPicker = false },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F2C59))
                        ) { Text("Aceptar", color = Color.White) }
                    }
                }
            }
        }
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
                modifier = Modifier.size(40.dp).background(Color(0xFFDBEAFE), RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFF155DFC), modifier = Modifier.size(20.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text("Contraseña Generada Automáticamente", fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color(0xFF1C398E))
                Spacer(Modifier.height(4.dp))
                Text("Al crear el usuario, se generará una contraseña segura automáticamente.", fontSize = 14.sp, color = Color(0xFF1447E6))
                Spacer(Modifier.height(8.dp))
                Text("✓ El usuario recibirá sus credenciales de acceso", fontSize = 12.sp, color = Color(0xFF155DFC))
                Text("✓ Se recomienda cambiar la contraseña en el primer inicio", fontSize = 12.sp, color = Color(0xFF155DFC))
            }
        }
    }
}
