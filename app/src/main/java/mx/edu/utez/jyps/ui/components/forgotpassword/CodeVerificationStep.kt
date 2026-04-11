package mx.edu.utez.jyps.ui.components.forgotpassword

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mx.edu.utez.jyps.ui.components.buttons.PrimaryButton
import mx.edu.utez.jyps.ui.components.inputs.AppTextField
import mx.edu.utez.jyps.viewmodel.ForgotPasswordUiState

/**
 * Stage 2: UI for verifying the security token sent to the user's email.
 * This component allows the user to input the UUID-based token and proceed to password setup.
 *
 * @param uiState Current view state containing the verification code, countdown, and error messages.
 * @param onCodeChange Callback triggered when the user updates the token text field.
 * @param onVerifyCode Action triggered when the primary button is clicked to submit the code.
 * @param onResendCode Action to request a new token if the previous one was lost or delayed.
 */
@Composable
fun CodeVerificationStep(
    uiState: ForgotPasswordUiState,
    onCodeChange: (String) -> Unit,
    onVerifyCode: () -> Unit,
    onResendCode: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "Token de Acceso",
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Start
        )
        
        Spacer(modifier = Modifier.height(8.dp))

        AppTextField(
            value = uiState.verificationCode,
            onValueChange = onCodeChange,
            label = "",
            placeholder = "5ca0722a-3d0c-4845-be30-fa8d91807f54",
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Código enviado a ${uiState.email}",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.secondary,
            textAlign = TextAlign.Center
        )
    }


    if (uiState.codeErrorMessage != null) {
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
                text = uiState.codeErrorMessage,
                color = Color(0xFFDC3545),
                fontSize = 14.sp
            )
        }
    }

    PrimaryButton(
        text = if (uiState.isLoading) "Verificando..." else "Verificar Código",
        onClick = onVerifyCode,
        enabled = !uiState.isLoading
    )

    TextButton(
        onClick = onResendCode,
        enabled = uiState.resendCountdown == 0 && !uiState.isLoading
    ) {
        Text(
            text = if (uiState.resendCountdown > 0) {
                "Reenviar en ${uiState.resendCountdown}s"
            } else {
                "Reenviar código"
            },
            color = if (uiState.resendCountdown > 0) Color.Gray else MaterialTheme.colorScheme.primary,
            fontSize = 14.sp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CodeVerificationStepPreview() {
    CodeVerificationStep(
        uiState = ForgotPasswordUiState(verificationCode = "123456"),
        onCodeChange = {},
        onVerifyCode = {},
        onResendCode = {}
    )
}
