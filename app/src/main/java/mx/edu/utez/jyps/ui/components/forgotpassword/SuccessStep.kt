package mx.edu.utez.jyps.ui.components.forgotpassword

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mx.edu.utez.jyps.ui.components.buttons.PrimaryButton

/**
 * Stage 4: Success confirmation UI for the password recovery flow.
 * Displays a celebratory icon and instructions for the user to return to the login screen.
 *
 * @param onBackToLoginClick Callback to navigate the user back to the authentication entry point.
 */
@Composable
fun SuccessStep(onBackToLoginClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(64.dp)
            .background(Color(0xFF28A745), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Filled.Check,
            contentDescription = "Éxito",
            tint = Color.White,
            modifier = Modifier.size(32.dp)
        )
    }

    Text(
        text = "Tu contraseña ha sido actualizada correctamente. Ya puedes acceder a tu cuenta.",
        fontSize = 16.sp,
        color = MaterialTheme.colorScheme.secondary,
        textAlign = TextAlign.Center,
        lineHeight = 24.sp
    )

    PrimaryButton(
        text = "Ir a Iniciar Sesión",
        onClick = onBackToLoginClick
    )
}

@Preview(showBackground = true)
@Composable
fun SuccessStepPreview() {
    SuccessStep(onBackToLoginClick = {})
}
