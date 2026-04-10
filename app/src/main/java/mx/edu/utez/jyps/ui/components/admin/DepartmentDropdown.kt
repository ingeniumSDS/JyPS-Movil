package mx.edu.utez.jyps.ui.components.admin

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import mx.edu.utez.jyps.data.model.DepartamentoResponse
import mx.edu.utez.jyps.ui.components.inputs.AppDropdown

/**
 * Dropdown component to select a department using the generic AppDropdown template.
 * Supports custom labels and error states for flexible form integration.
 *
 * @param label The descriptive text shown above the dropdown.
 * @param departamentos Real list of department models dynamically passed from network.
 * @param selectedId The currently tracked database identifier for the selection.
 * @param onSelect Callback executed when the user chooses a specific department.
 * @param errorMessage Optional validation message displayed when the selection is invalid.
 */
@Composable
fun DepartmentDropdown(
    label: String = "Departamento *",
    departamentos: List<DepartamentoResponse>,
    selectedId: Long,
    onSelect: (Long) -> Unit,
    errorMessage: String? = null
) {
    // Map internal IDs to display names
    val items = if (departamentos.isNotEmpty()) {
        departamentos.map { it.id to it.nombre }
    } else {
        listOf(0L to "Cargando...")
    }

    val selectedName = items.find { it.first == selectedId }?.second ?: "Seleccionar departamento"

    AppDropdown(
        label = label,
        options = items.map { it.second },
        selectedOption = selectedName,
        onOptionSelected = { name ->
            val id = items.find { it.second == name }?.first ?: 0L
            onSelect(id)
        },
        leadingIcon = Icons.Default.Business,
        error = errorMessage
    )
}

@Preview(showBackground = true)
@Composable
fun DepartmentDropdownPreview() {
    DepartmentDropdown(
        label = "Departamento de Prueba",
        departamentos = emptyList(),
        selectedId = 0L, 
        onSelect = {}
    )
}
