package mx.edu.utez.jyps.ui.components.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mx.edu.utez.jyps.ui.theme.JyPSTheme

@Composable
fun TestUsersBox(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(8.dp),
                ambientColor = Color(0x14000000), // 8% opacity 
                spotColor = Color(0x14000000)
            )
            .background(Color.White, RoundedCornerShape(8.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Usuarios de prueba:",
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            TestUserRow("Empleado", "juan.perez@utez.edu.mx")
            TestUserRow("Guardia", "maria.gonzalez@utez.edu.mx")
            TestUserRow("Jefe de Departamento", "roberto.sanchez@utez.edu.mx")
            TestUserRow("Recursos Humanos", "laura.martinez@utez.edu.mx")
            TestUserRow("Administrador", "carlos.rodriguez@utez.edu.mx")
            TestUserRow(
                role = "Cuenta Inactiva",
                email = "pedro.ramirez@utez.edu.mx",
                isError = true
            )
        }

        Text(
            text = "Contraseña: cualquiera",
            fontStyle = FontStyle.Italic,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
private fun TestUserRow(
    role: String,
    email: String,
    isError: Boolean = false
) {
    Text(
        text = "• $role: $email",
        fontSize = 12.sp,
        color = if (isError) Color(0xFFE7000B) else Color(0xFF6A7282),
        lineHeight = 16.sp
    )
}

@Preview(showBackground = true)
@Composable
fun TestUsersBoxPreview() {
    JyPSTheme {
        TestUsersBox(modifier = Modifier.padding(24.dp))
    }
}
