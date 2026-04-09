package mx.edu.utez.jyps.ui.components.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LoginFooterLinks() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "¿No tienes cuenta?",
            fontSize = 14.sp,
            color = Color(0xFF4A5565),
            textAlign = TextAlign.Center
        )
        Text(
            text = "Comunícate con el administrador del sistema.",
            fontSize = 14.sp,
            color = Color(0xFF4A5565),
            textAlign = TextAlign.Center
        )
    }
}


@Preview(showBackground = true)
@Composable
fun LoginFooterLinksPreview() {
    LoginFooterLinks()
}
