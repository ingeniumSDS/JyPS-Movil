package mx.edu.utez.jyps.ui.components.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import mx.edu.utez.jyps.ui.components.admin.DialogHeader
import mx.edu.utez.jyps.ui.components.buttons.PrimaryButton
import mx.edu.utez.jyps.ui.components.inputs.AppTextField
import mx.edu.utez.jyps.ui.components.inputs.AppDropdown
import mx.edu.utez.jyps.viewmodel.DepartmentManagementViewModel

/**
 * High-fidelity Dialog for editing an existing department.
 * Mirrors the large-dialog pattern for consistency across the Admin module.
 *
 * @param viewModel ViewModel controlling the edit form state.
 */
@Composable
fun EditDepartmentDialog(viewModel: DepartmentManagementViewModel) {
    val isVisible by viewModel.isEditVisible.collectAsStateWithLifecycle()
    val name by viewModel.formName.collectAsStateWithLifecycle()
    val description by viewModel.formDescription.collectAsStateWithLifecycle()
    val selectedJefeId by viewModel.formJefeId.collectAsStateWithLifecycle()
    val availableHeads by viewModel.availableHeads.collectAsStateWithLifecycle()
    val currentDept by viewModel.selectedDept.collectAsStateWithLifecycle()
    val errors by viewModel.formErrors.collectAsStateWithLifecycle()

    val jefeOptions = remember(availableHeads, currentDept) {
        val baseOptions = mutableListOf(0L to "Sin jefe asignado")
        
        // El actual jefe debe estar siempre por si se quiere dejar igual
        currentDept?.let { dept ->
            if (dept.jefeId != null && dept.jefeId > 0) {
                baseOptions.add(dept.jefeId to (dept.nombreJefe ?: "Jefe Actual"))
            }
        }
        
        // Agregamos los disponibles filtrados evitando duplicados
        val heads = availableHeads.map { it.id to it.nombreCompleto }
        heads.forEach { head ->
            if (baseOptions.none { it.first == head.first }) {
                baseOptions.add(head)
            }
        }
        
        baseOptions.toList()
    }
    
    val selectedJefeName = jefeOptions.find { it.first == (selectedJefeId ?: 0L) }?.second 
        ?: currentDept?.nombreJefe 
        ?: "Sin jefe asignado"

    if (isVisible) {
        Dialog(
            onDismissRequest = { viewModel.closeEdit() },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.4f)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 24.dp)
                        .background(Color.White, RoundedCornerShape(12.dp))
                        .fillMaxWidth()
                ) {
                    DialogHeader(title = "Editar Departamento", onClose = viewModel::closeEdit)

                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        AppTextField(
                            value = name,
                            onValueChange = viewModel::onFormNameChange,
                            label = "Nombre del departamento *",
                            placeholder = "Ej: Tecnologías de la Información",
                            modifier = Modifier.fillMaxWidth()
                        )
                        if (errors.containsKey("nombre")) {
                            Text(errors["nombre"]!!, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                        }

                        AppTextField(
                            value = description,
                            onValueChange = viewModel::onFormDescriptionChange,
                            label = "Descripción *",
                            placeholder = "Describe las funciones y responsabilidades del departamento...",
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = false,
                            minLines = 3
                        )
                        if (errors.containsKey("descripcion")) {
                            Text(errors["descripcion"]!!, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                        }

                        AppDropdown(
                            label = "Jefe del departamento (Opcional)",
                            options = jefeOptions.map { it.second },
                            selectedOption = selectedJefeName,
                            onOptionSelected = { name ->
                                val id = jefeOptions.find { it.second == name }?.first
                                viewModel.onFormJefeSelected(if (id == 0L) null else id)
                            },
                            leadingIcon = Icons.Default.Person,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        HorizontalDivider(color = Color(0xFFE5E7EB))
                        Spacer(modifier = Modifier.height(8.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedButton(
                                onClick = { viewModel.closeEdit() },
                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                shape = RoundedCornerShape(8.dp),
                                border = androidx.compose.foundation.BorderStroke(1.7.dp, Color(0xFF0F2C59))
                            ) {
                                Text("Cancelar", color = Color(0xFF0F2C59), fontWeight = FontWeight.Medium)
                            }

                            PrimaryButton(
                                text = "Guardar Cambios",
                                onClick = { viewModel.saveDepartment() },
                                backgroundColor = Color(0xFF28A745),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }
}
