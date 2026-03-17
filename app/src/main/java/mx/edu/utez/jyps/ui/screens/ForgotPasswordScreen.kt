package mx.edu.utez.jyps.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import mx.edu.utez.jyps.ui.components.buttons.PrimaryButton
import mx.edu.utez.jyps.ui.components.inputs.AppTextField
import mx.edu.utez.jyps.ui.components.inputs.VerificationCodeInput
import mx.edu.utez.jyps.ui.theme.JyPSTheme
import mx.edu.utez.jyps.viewmodel.ForgotPasswordStep
import mx.edu.utez.jyps.viewmodel.ForgotPasswordUiState
import mx.edu.utez.jyps.viewmodel.ForgotPasswordViewModel

@Composable
fun ForgotPasswordScreen(
    viewModel: ForgotPasswordViewModel,
    onBackToLoginClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ForgotPasswordContent(
        uiState = uiState,
        onEmailChange = viewModel::onEmailChange,
        onCodeChange = viewModel::onCodeChange,
        onSubmitEmail = viewModel::submitEmail,
        onVerifyCode = viewModel::verifyCode,
        onBackToLoginClick = {
            viewModel.resetState()
            onBackToLoginClick()
        }
    )
}

@Composable
fun ForgotPasswordContent(
    uiState: ForgotPasswordUiState,
    onEmailChange: (String) -> Unit,
    onCodeChange: (String) -> Unit,
    onSubmitEmail: () -> Unit,
    onVerifyCode: () -> Unit,
    onBackToLoginClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Shared Header Header Logo (Graduation Cap from Figma)
        Box(
            modifier = Modifier
                .size(64.dp)
                .background(color = MaterialTheme.colorScheme.primary, shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Check, // Placeholder for GraduationCap
                contentDescription = "Recuperar Contraseña Icono",
                modifier = Modifier.size(32.dp),
                tint = Color.White
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Dynamic Title & Subtitle based on step
        val title = when (uiState.currentStep) {
            ForgotPasswordStep.EMAIL_INPUT, ForgotPasswordStep.CODE_VERIFICATION -> "Recuperar Contraseña"
            ForgotPasswordStep.SUCCESS -> "¡Listo!"
        }
        val subtitle = when (uiState.currentStep) {
            ForgotPasswordStep.EMAIL_INPUT -> "Ingresa tu correo electrónico"
            ForgotPasswordStep.CODE_VERIFICATION -> "Ingresa el código enviado a tu correo"
            ForgotPasswordStep.SUCCESS -> "Contraseña restablecida exitosamente"
        }

        Text(
            text = title,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        
        Text(
            text = subtitle,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.secondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Content Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(32.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                when (uiState.currentStep) {
                    ForgotPasswordStep.EMAIL_INPUT -> {
                        EmailInputStep(uiState, onEmailChange, onSubmitEmail)
                    }
                    ForgotPasswordStep.CODE_VERIFICATION -> {
                        CodeVerificationStep(uiState, onCodeChange, onVerifyCode)
                    }
                    ForgotPasswordStep.SUCCESS -> {
                        SuccessStep(onBackToLoginClick)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Global Back Button (except on success)
        if (uiState.currentStep != ForgotPasswordStep.SUCCESS) {
            TextButton(onClick = onBackToLoginClick) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Volver",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Volver al inicio de sesión",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun EmailInputStep(
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

@Composable
private fun CodeVerificationStep(
    uiState: ForgotPasswordUiState,
    onCodeChange: (String) -> Unit,
    onVerifyCode: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "Código de Verificación",
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(8.dp))

        VerificationCodeInput(
            value = uiState.verificationCode,
            onValueChange = onCodeChange,
            isError = uiState.codeErrorMessage != null,
            modifier = Modifier.widthIn(max = 400.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Código enviado a ${uiState.email}",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.secondary,
            textAlign = TextAlign.Center
        )
    }

    // Dummy Help Message
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFEFF6FF), RoundedCornerShape(8.dp))
            .border(1.dp, Color(0xFFBEDBFF), RoundedCornerShape(8.dp))
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Código de prueba: 218811", color = Color(0xFF193CB8), fontWeight = FontWeight.Bold, fontSize = 12.sp)
        Text(text = "(Este código se muestra solo en entorno de desarrollo)", color = Color(0xFF155DFC), fontSize = 12.sp)
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

@Composable
private fun SuccessStep(onBackToLoginClick: () -> Unit) {
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

// Previews
@Preview(showBackground = true)
@Composable
fun ForgotPasswordEmailStepPreview() {
    JyPSTheme {
        ForgotPasswordContent(
            uiState = ForgotPasswordUiState(currentStep = ForgotPasswordStep.EMAIL_INPUT),
            onEmailChange = {}, onCodeChange = {}, onSubmitEmail = {}, onVerifyCode = {}, onBackToLoginClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ForgotPasswordCodeStepPreview() {
    JyPSTheme {
        ForgotPasswordContent(
            uiState = ForgotPasswordUiState(currentStep = ForgotPasswordStep.CODE_VERIFICATION, email = "test@utez.edu.mx"),
            onEmailChange = {}, onCodeChange = {}, onSubmitEmail = {}, onVerifyCode = {}, onBackToLoginClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ForgotPasswordSuccessStepPreview() {
    JyPSTheme {
        ForgotPasswordContent(
            uiState = ForgotPasswordUiState(currentStep = ForgotPasswordStep.SUCCESS),
            onEmailChange = {}, onCodeChange = {}, onSubmitEmail = {}, onVerifyCode = {}, onBackToLoginClick = {}
        )
    }
}
