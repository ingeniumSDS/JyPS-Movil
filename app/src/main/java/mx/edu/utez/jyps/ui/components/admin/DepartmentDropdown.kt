package mx.edu.utez.jyps.ui.components.admin

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.tooling.preview.Preview
import mx.edu.utez.jyps.data.model.Departamento

/**
 * Dropdown component to select a department.
 *
 * @param departamentos Real list of models dynamically passed.
 * @param selectedId The currently tracked department Id.
 * @param onSelect Callback when user selects an item.
 * @param onExpand Optional callback when dropdown opens to trigger network fetch.
 */
@Composable
fun DepartmentDropdown(
    departamentos: List<Departamento>,
    selectedId: Int,
    onSelect: (Int) -> Unit,
    onExpand: () -> Unit = {}
) {
    var expanded by remember { mutableStateOf(false) }

    LaunchedEffect(expanded) {
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

@Preview(showBackground = true)
@Composable
fun DepartmentDropdownPreview() {
    DepartmentDropdown(departamentos = emptyList(), selectedId = 1, onSelect = {})
}
