package mx.edu.utez.jyps.ui.components.dialogs

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.tooling.preview.Preview
import mx.edu.utez.jyps.ui.components.admin.DialogHeader
import mx.edu.utez.jyps.ui.components.admin.ErrorBanner
import mx.edu.utez.jyps.ui.components.admin.FormField
import mx.edu.utez.jyps.ui.components.admin.RolesSelector
import mx.edu.utez.jyps.ui.components.admin.DepartmentDropdown
import mx.edu.utez.jyps.ui.components.admin.TimeFieldWithPicker
import mx.edu.utez.jyps.ui.components.admin.PasswordInfoBox
import mx.edu.utez.jyps.viewmodel.AdminViewModel

/**
 * Dialog screen overlay for creating a new user.
 * Interacts with AdminViewModel to collect form state and dispatch creation requests.
 *
 * @param viewModel The ViewModel handling admin business logic.
 */
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

                    LaunchedEffect(scrollToTopTrigger) {
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

                        // Actions: Stacked buttons for consistency
                        Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedButton(
                                onClick = { viewModel.setCreateUserVisible(false) },
                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.5.dp, Color(0xFF0F2C59))
                            ) { 
                                Text("Cancelar", color = Color(0xFF0F2C59), fontWeight = FontWeight.Medium) 
                            }

                            Button(
                                onClick = { viewModel.saveNewUser() },
                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF28A745))
                            ) { 
                                Text("Crear Usuario", color = Color.White, fontWeight = FontWeight.Medium) 
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CreateUserDialogPreview() {
    // Requires mocked AdminViewModel
}
