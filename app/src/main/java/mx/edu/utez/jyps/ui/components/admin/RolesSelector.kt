package mx.edu.utez.jyps.ui.components.admin

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview

/**
 * Roles checkbox group component.
 *
 * @param selectedRoles A set containing active role IDs.
 * @param onToggleRole Callback fired when a role is clicked.
 * @param errorMessage Message to show as an error description underneath.
 */
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

@Preview(showBackground = true)
@Composable
fun RolesSelectorPreview() {
    RolesSelector(selectedRoles = setOf(1, 4), onToggleRole = {})
}
