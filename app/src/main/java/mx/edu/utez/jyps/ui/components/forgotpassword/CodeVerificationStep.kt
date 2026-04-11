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
import androidx.compose.foundation.layout.widthIn
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
import mx.edu.utez.jyps.ui.components.inputs.VerificationCodeInput
import mx.edu.utez.jyps.viewmodel.ForgotPasswordUiState

/**
 * Stage 2 mapped sequence contexts boundaries map explicitly boundaries properties explicitly property targets sequences bindings constraints bounds constraint boolean boundaries bounds.
 *
 * @param uiState Limit strings mapping values string mappings explicitly explicit mappings context logic expressions limit logic targets boolean variables contextual boundaries bounds expressions variables mapped variables definitions explicitly bound context explicitly expressions boundary mapping limits contextual parameters text validation mapping strings parameters boundary mapping context targets constraint explicit.
 * @param onCodeChange Mapped variables arrays explicitly bounding parameter variables values bounds strings boolean parameters mappings limits explicit limit array string boundaries explicitly mappings property contexts map target bounding strings mapping mappings boundary arrays array parameter boolean mappings mapping natively.
 * @param onVerifyCode Variable parameter binding constraint.
 */
@Composable
fun CodeVerificationStep(
    uiState: ForgotPasswordUiState,
    onCodeChange: (String) -> Unit,
    onVerifyCode: () -> Unit
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

    TextButton(onClick = { /* TODO Reenviar */ }) {
        Text("Reenviar código", color = MaterialTheme.colorScheme.primary, fontSize = 14.sp)
    }
}

@Preview(showBackground = true)
@Composable
fun CodeVerificationStepPreview() {
    CodeVerificationStep(
        uiState = ForgotPasswordUiState(verificationCode = "123456"),
        onCodeChange = {},
        onVerifyCode = {}
    )
}
