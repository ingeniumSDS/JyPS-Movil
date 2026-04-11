package mx.edu.utez.jyps.ui.components.forgotpassword

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mx.edu.utez.jyps.ui.components.buttons.PrimaryButton
import mx.edu.utez.jyps.ui.components.inputs.AppTextField
import mx.edu.utez.jyps.viewmodel.ForgotPasswordUiState

/**
 * Stage 3: UI for setting the new password after token validation.
 * Enforces complexity rules including length, casing, numbers, and special characters.
 * 
 * @param uiState Current view state holding the double-password input fields and error states.
 * @param onPasswordChange Callback for the initial new password field.
 * @param onConfirmPasswordChange Callback for the secondary confirmation field.
 * @param onSubmit Action to trigger the final credential reconciliation with the backend.
 */
@Composable
fun PasswordSetupStep(
    uiState: ForgotPasswordUiState,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onSubmit: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            AppTextField(
                value = uiState.newPassword,
                onValueChange = onPasswordChange,
                label = "Nueva Contraseña",
                placeholder = "********",
                isPassword = true
            )
            Text(
                text = "Mínimo 12 caracteres. Debe incluir mayúscula, minúscula, número y un carácter especial.",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 16.sp
            )
        }

        AppTextField(
            value = uiState.confirmPassword,
            onValueChange = onConfirmPasswordChange,
            label = "Confirmar Contraseña",
            placeholder = "********",
            isPassword = true
        )

        if (uiState.passwordErrorMessage != null) {
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
                    text = uiState.passwordErrorMessage,
                    color = Color(0xFFDC3545),
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            }
        }

        PrimaryButton(
            text = if (uiState.isLoading) "Restableciendo..." else "Restablecer Contraseña",
            onClick = onSubmit,
            enabled = !uiState.isLoading && uiState.newPassword.isNotEmpty() && uiState.confirmPassword.isNotEmpty()
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PasswordSetupStepPreview() {
    PasswordSetupStep(
        uiState = ForgotPasswordUiState(),
        onPasswordChange = {},
        onConfirmPasswordChange = {},
        onSubmit = {}
    )
}
