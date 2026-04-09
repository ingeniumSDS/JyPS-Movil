package mx.edu.utez.jyps.ui.components.forgotpassword

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mx.edu.utez.jyps.ui.components.buttons.PrimaryButton
import mx.edu.utez.jyps.ui.components.inputs.AppTextField
import mx.edu.utez.jyps.viewmodel.ForgotPasswordUiState

/**
 * Stage 1 map context mappings values bounds context map boolean context limits implicitly mapped definition logic boundary sequence properties mappings limits variables text mapping limits string mappings boolean maps mappings natively limits natively values bounds expressions arrays boundary expressions textual map explicitly mapped constraints contexts.
 *
 * @param uiState Limit constraints.
 * @param onEmailChange Boolean explicit definition sequence bound limits definitions boundary explicitly expression limits.
 * @param onSubmitEmail Mapped parameter string sequences limits mapped mapped parameters explicit values property boolean contexts explicitly mappings properties definition constraints bounding mappings mapped.
 */
@Composable
fun EmailInputStep(
    uiState: ForgotPasswordUiState,
    onEmailChange: (String) -> Unit,
    onSubmitEmail: () -> Unit
) {
    AppTextField(
        value = uiState.email,
        onValueChange = onEmailChange,
        label = "Correo Electrónico",
        placeholder = "tu@utez.edu.mx"
    )

    if (uiState.emailErrorMessage != null) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFFEF2F2), RoundedCornerShape(8.dp))
                .border(1.dp, Color(0xFFFFC9C9), RoundedCornerShape(8.dp))
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Warning,
                contentDescription = "Error",
                tint = Color(0xFFDC3545),
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = uiState.emailErrorMessage,
                color = Color(0xFFDC3545),
                fontSize = 14.sp,
                lineHeight = 20.sp
            )
        }
    }

    PrimaryButton(
        text = if (uiState.isLoading) "Enviando..." else "Enviar Código",
        onClick = onSubmitEmail,
        enabled = !uiState.isLoading
    )
}

@Preview(showBackground = true)
@Composable
fun EmailInputStepPreview() {
    EmailInputStep(
        uiState = ForgotPasswordUiState(),
        onEmailChange = {},
        onSubmitEmail = {}
    )
}
