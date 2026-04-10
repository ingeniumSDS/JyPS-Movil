package mx.edu.utez.jyps.ui.components.admin

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import mx.edu.utez.jyps.data.model.DepartamentoResponse
import mx.edu.utez.jyps.ui.components.inputs.AppDropdown

/**
 * Dropdown component to select a department using the generic AppDropdown template.
 *
 * @param departamentos Real list of models dynamically passed.
 * @param selectedId The currently tracked department Id.
 * @param onSelect Callback when user selects an item.
 */
@Composable
fun DepartmentDropdown(
    departamentos: List<DepartamentoResponse>,
    selectedId: Long,
    onSelect: (Long) -> Unit
) {
    val items = if (departamentos.isNotEmpty()) {
        departamentos.map { it.id to it.nombre }
    } else {
        listOf(0L to "Cargando...")
    }

    val selectedName = items.find { it.first == selectedId }?.second ?: "Seleccionar departamento"

    AppDropdown(
        label = "Departamento *",
        options = items.map { it.second },
        selectedOption = selectedName,
        onOptionSelected = { name ->
            val id = items.find { it.second == name }?.first ?: 0L
            onSelect(id)
        },
        leadingIcon = Icons.Default.Business
    )
}

@Preview(showBackground = true)
@Composable
fun DepartmentDropdownPreview() {
    DepartmentDropdown(departamentos = emptyList(), selectedId = 0L, onSelect = {})
}
