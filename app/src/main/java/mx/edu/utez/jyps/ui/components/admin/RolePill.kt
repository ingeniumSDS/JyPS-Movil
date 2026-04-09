package mx.edu.utez.jyps.ui.components.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Displays the user's role dynamically styled.
 *
 * @param role The role name to display as text.
 */
@Composable
fun RolePill(role: String) {
    val (bgColor, textColor) = when (role) {
        "Administrador" -> Pair(Color(0xFFFFE2E2), Color(0xFFC10007))
        "Empleado" -> Pair(Color(0xFFDBEAFE), Color(0xFF1447E6))
        "Jefe de Departamento" -> Pair(Color(0xFFF3E8FF), Color(0xFF8200DB))
        "Auditor" -> Pair(Color(0xFFDCFCE7), Color(0xFF008236))
        "Guardia" -> Pair(Color(0xFFFFEDD4), Color(0xFFCA3500))
        else -> Pair(Color(0xFFF3F4F6), Color(0xFF4A5565))
    }
    Box(modifier = Modifier.background(bgColor, RoundedCornerShape(12.dp)).padding(horizontal = 12.dp, vertical = 4.dp)) {
        Text(text = role, color = textColor, fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}

@Preview
@Composable
fun RolePillPreview() {
    RolePill(role = "Administrador")
}
