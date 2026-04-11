package mx.edu.utez.jyps.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
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
import mx.edu.utez.jyps.ui.components.forgotpassword.CodeVerificationStep
import mx.edu.utez.jyps.ui.components.forgotpassword.EmailInputStep
import mx.edu.utez.jyps.ui.components.forgotpassword.PasswordSetupStep
import mx.edu.utez.jyps.ui.components.forgotpassword.SuccessStep
import androidx.compose.material.icons.filled.Lock
import mx.edu.utez.jyps.ui.theme.JyPSTheme
import mx.edu.utez.jyps.viewmodel.ForgotPasswordStep
import mx.edu.utez.jyps.viewmodel.ForgotPasswordUiState
import mx.edu.utez.jyps.viewmodel.ForgotPasswordViewModel

/**
 * Screen component for the Password Recovery flow.
 * Manages the state and transition between email input, code verification, and success.
 *
 * @param viewModel The ViewModel handling the recovery logic and state.
 * @param onBackToLoginClick Callback to navigate the user back to the login screen.
 */
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
        onResendCode = viewModel::resendCode,
        onPasswordChange = viewModel::onPasswordChange,
        onConfirmPasswordChange = viewModel::onConfirmPasswordChange,
        onConfirmPasswordReset = viewModel::confirmPasswordReset,
        onBackToLoginClick = {
            viewModel.resetState()
            onBackToLoginClick()
        }
    )
}

/**
 * Core content provider for the Forgot Password flow.
 * Manages the layout and rendering of different recovery steps within a centralized card.
 *
 * @param uiState Unified view state from the ViewModel.
 * @param onEmailChange Callback for the initial identity identification stage.
 * @param onCodeChange Callback for the token validation security stage.
 * @param onSubmitEmail Action to trigger the primary identity lookup.
 * @param onVerifyCode Action to trigger token reconciliation with the provider.
 * @param onResendCode Action to trigger a session identity token re-generation.
 * @param onPasswordChange Callback for the new credential setup.
 * @param onConfirmPasswordChange Callback for the credential confirmation field.
 * @param onConfirmPasswordReset Action to finalize the account recovery process.
 * @param onBackToLoginClick Action to purge the current recovery state and return to login.
 */
@Composable
fun ForgotPasswordContent(
    uiState: ForgotPasswordUiState,
    onEmailChange: (String) -> Unit,
    onCodeChange: (String) -> Unit,
    onSubmitEmail: () -> Unit,
    onVerifyCode: () -> Unit,
    onResendCode: () -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onConfirmPasswordReset: () -> Unit,
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
                .background(color = Color(0xFF0F2C59), shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = when(uiState.currentStep) {
                    ForgotPasswordStep.SUCCESS -> Icons.Filled.Check
                    else -> Icons.Filled.Lock
                },
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = Color.White
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Dynamic Title & Subtitle based on step
        val title = when (uiState.currentStep) {
            ForgotPasswordStep.EMAIL_INPUT, 
            ForgotPasswordStep.CODE_VERIFICATION -> "Recuperar Contraseña"
            ForgotPasswordStep.PASSWORD_SETUP -> "Nueva Contraseña"
            ForgotPasswordStep.SUCCESS -> "¡Listo!"
        }
        val subtitle = when (uiState.currentStep) {
            ForgotPasswordStep.EMAIL_INPUT -> "Ingresa tu correo electrónico"
            ForgotPasswordStep.CODE_VERIFICATION -> "Ingresa el token enviado a tu correo"
            ForgotPasswordStep.PASSWORD_SETUP -> "Crea una nueva contraseña segura y fácil de recordar"
            ForgotPasswordStep.SUCCESS -> "¡Actualización completada!"
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
                        CodeVerificationStep(uiState, onCodeChange, onVerifyCode, onResendCode)
                    }
                    ForgotPasswordStep.PASSWORD_SETUP -> {
                        PasswordSetupStep(
                            uiState = uiState,
                            onPasswordChange = onPasswordChange,
                            onConfirmPasswordChange = onConfirmPasswordChange,
                            onSubmit = onConfirmPasswordReset
                        )
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

// Previews
@Preview(showBackground = true)
@Composable
fun ForgotPasswordEmailStepPreview() {
    JyPSTheme {
        ForgotPasswordContent(
            uiState = ForgotPasswordUiState(currentStep = ForgotPasswordStep.EMAIL_INPUT),
            onEmailChange = {}, onCodeChange = {}, onSubmitEmail = {}, onVerifyCode = {}, 
            onResendCode = {}, onPasswordChange = {}, onConfirmPasswordChange = {}, 
            onConfirmPasswordReset = {}, onBackToLoginClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ForgotPasswordCodeStepPreview() {
    JyPSTheme {
        ForgotPasswordContent(
            uiState = ForgotPasswordUiState(currentStep = ForgotPasswordStep.CODE_VERIFICATION, email = "test@utez.edu.mx"),
            onEmailChange = {}, onCodeChange = {}, onSubmitEmail = {}, onVerifyCode = {}, 
            onResendCode = {}, onPasswordChange = {}, onConfirmPasswordChange = {}, 
            onConfirmPasswordReset = {}, onBackToLoginClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ForgotPasswordSuccessStepPreview() {
    JyPSTheme {
        ForgotPasswordContent(
            uiState = ForgotPasswordUiState(currentStep = ForgotPasswordStep.SUCCESS),
            onEmailChange = {}, onCodeChange = {}, onSubmitEmail = {}, onVerifyCode = {}, 
            onResendCode = {}, onPasswordChange = {}, onConfirmPasswordChange = {}, 
            onConfirmPasswordReset = {}, onBackToLoginClick = {}
        )
    }
}
