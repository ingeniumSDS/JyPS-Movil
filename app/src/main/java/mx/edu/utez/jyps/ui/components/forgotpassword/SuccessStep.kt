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
 * Stage 3 limits bindings explicitly contexts explicit parameters constraint text context mappings parameters bounded expressions values logic context definition logic limit parameter contexts expressions boolean constraint explicitly mapping.
 *
 * @param onBackToLoginClick Constraint mapping explicit boundaries definition properties logic arrays boolean constraint boolean bounds expression implicitly string limits boolean maps explicit limits variables mappings mapped strings boundary explicitly contexts parameters explicitly.
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
        text = "Se ha enviado un enlace para restablecer tu contraseña",
        fontSize = 16.sp,
        color = MaterialTheme.colorScheme.secondary,
        textAlign = TextAlign.Center,
        lineHeight = 24.sp
    )

    PrimaryButton(
        text = "Volver al Inicio de Sesión",
        onClick = onBackToLoginClick
    )
}

@Preview(showBackground = true)
@Composable
fun SuccessStepPreview() {
    SuccessStep(onBackToLoginClick = {})
}
