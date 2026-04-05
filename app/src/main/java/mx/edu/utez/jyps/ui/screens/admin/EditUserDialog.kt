package mx.edu.utez.jyps.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
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
fun EditUserDialog(viewModel: AdminViewModel) {
    val isVisible by viewModel.isEditUserVisible.collectAsStateWithLifecycle()

    if (isVisible) {
        Dialog(
            onDismissRequest = { viewModel.closeEditUser() },
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
                    DialogHeader(title = "Editar Usuario", onClose = { viewModel.closeEditUser() })

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
                        val name by viewModel.editName.collectAsStateWithLifecycle()
                        val paterno by viewModel.editPaterno.collectAsStateWithLifecycle()
                        val materno by viewModel.editMaterno.collectAsStateWithLifecycle()
                        val phone by viewModel.editPhone.collectAsStateWithLifecycle()
                        val email by viewModel.editEmail.collectAsStateWithLifecycle()
                        val formErrors by viewModel.editFormErrors.collectAsStateWithLifecycle()
                        val serverError by viewModel.editServerResponseError.collectAsStateWithLifecycle()

                        // Error banner
                        serverError?.let { ErrorBanner(it) }

                        // Name fields — stacked vertically for mobile
                        FormField(label = "Nombre *", value = name, onValueChange = viewModel::onEditNameChange, errorMessage = formErrors["nombre"])
                        FormField(label = "Apellido Paterno *", value = paterno, onValueChange = viewModel::onEditPaternoChange, errorMessage = formErrors["paterno"])
                        FormField(label = "Apellido Materno *", value = materno, onValueChange = viewModel::onEditMaternoChange, errorMessage = formErrors["materno"])
                        FormField(label = "Teléfono *", value = phone, placeholder = "777 123 4567", onValueChange = viewModel::onEditPhoneChange, errorMessage = formErrors["telefono"])
                        FormField(label = "Email institucional *", value = email, placeholder = "usuario@utez.edu.mx", onValueChange = viewModel::onEditEmailChange, errorMessage = formErrors["email"])

                        // Roles
                        val selectedRoles by viewModel.editRoles.collectAsStateWithLifecycle()
                        RolesSelector(selectedRoles = selectedRoles, onToggleRole = viewModel::toggleEditRole, errorMessage = formErrors["roles"])

                        // Conditional: Department dropdown
                        val showDepartment = selectedRoles.contains(1) || selectedRoles.contains(3)
                        if (showDepartment) {
                            val departamentos by viewModel.departamentos.collectAsStateWithLifecycle()
                            val selectedDeptId by viewModel.editDepartmentId.collectAsStateWithLifecycle()
                            DepartmentDropdown(
                                departamentos = departamentos,
                                selectedId = selectedDeptId,
                                onSelect = viewModel::onEditDepartmentChange,
                                onExpand = viewModel::loadDepartamentos
                            )
                        }

                        // Conditional: Schedule — stacked vertically
                        val showSchedule = selectedRoles.contains(1)
                        if (showSchedule) {
                            val startH by viewModel.editStartHour.collectAsStateWithLifecycle()
                            val startM by viewModel.editStartMinute.collectAsStateWithLifecycle()
                            val endH by viewModel.editEndHour.collectAsStateWithLifecycle()
                            val endM by viewModel.editEndMinute.collectAsStateWithLifecycle()

                            TimeFieldWithPicker(label = "Hora Inicio Jornada *", hour = startH, minute = startM, onTimeSelected = viewModel::onEditStartTimeChange)
                            TimeFieldWithPicker(label = "Hora Fin Jornada *", hour = endH, minute = endM, onTimeSelected = viewModel::onEditEndTimeChange)
                        }

                        // Actions: 3 buttons — Responsive layout
                        Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            // Primary Action: Guardar
                            Button(
                                onClick = { viewModel.saveEditUser() },
                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F2C59))
                            ) {
                                Text("Guardar Cambios", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            }

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                OutlinedButton(
                                    onClick = { viewModel.closeEditUser() },
                                    modifier = Modifier.weight(1f).height(48.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF6A7282))
                                ) {
                                    Text("Cancelar", color = Color(0xFF6A7282), fontWeight = FontWeight.Medium, fontSize = 14.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
