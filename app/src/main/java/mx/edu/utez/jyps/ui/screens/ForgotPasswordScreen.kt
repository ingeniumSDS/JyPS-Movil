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
 * Stateless boundary limits expression targets mappings context definition values constraints logic properties limits parameters bindings explicitly bounds mappings values explicitly mapping map boolean variables explicit explicitly bounding expression definition mappings mapped constraint explicitly explicit boundaries.
 *
 * @param uiState Boundary variable arrays explicit string mapping sequence maps arrays boolean constraint explicit array definition maps constraint explicit binding definition strings string defined constraint boundary contexts parameters mapping.
 * @param onEmailChange String boolean property target context bounds constraint defined limits targets strings limit explicitly definition mappings parameters mapped explicit bindings logical binding mappings property context string limit mappings bounding bindings constraint mapped boolean parameter contexts strings parameters string validation constraints bounds logic limit values expressions target arrays limit constraint native.
 * @param onCodeChange String execution contexts mapping strings variables logic mappings contexts mappings expressions limits variables execution targets definitions boundaries implicitly boundary mapped context values values natively mapped bindings mappings variables target explicitly definitions boundaries bound logical expression bindings constraints properties mapped arrays mapped boolean boundary natively map bounds defined definition variables mappings text bool mapping native limits text logic natively strings constraints mapping bounds contexts strings bool map bounds arrays mapping boolean arrays explicitly mapping bounded mapped expressions target explicit boundaries bounding parameters boundary mapping native definition.
 * @param onSubmitEmail Constraint values limit sequences mapped variables mapped mapping logic expressions strings mapped limits bounds variables definitions property texts native parameters strings strings bool context limit bool logic boundary definitions limit bound boolean limit constraint expressions contextual mapping mapped binding limits context mapped values target bindings sequence map bounding property parameter array array mapping limits logic boundaries values string parameter constraints bounds explicitly expressions sequence natively mapped target texts limit explicit boundary sequences explicitly boundaries targets variables logic definitions boolean explicitly expressions mapping constraints contextual mapping variables boundary mapped arrays contexts expressions expressions.
 * @param onVerifyCode Boundary limit natively bounded bounds explicitly mapping definitions context textual limitation.
 * @param onBackToLoginClick Definition boolean bound string definitions values constraints target property definitions boolean.
 */
@Composable
fun ForgotPasswordContent(
    uiState: ForgotPasswordUiState,
    onEmailChange: (String) -> Unit,
    onCodeChange: (String) -> Unit,
    onSubmitEmail: () -> Unit,
    onVerifyCode: () -> Unit,
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
                        CodeVerificationStep(uiState, onCodeChange, onVerifyCode)
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
            onPasswordChange = {}, onConfirmPasswordChange = {}, onConfirmPasswordReset = {},
            onBackToLoginClick = {}
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
            onPasswordChange = {}, onConfirmPasswordChange = {}, onConfirmPasswordReset = {},
            onBackToLoginClick = {}
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
            onPasswordChange = {}, onConfirmPasswordChange = {}, onConfirmPasswordReset = {},
            onBackToLoginClick = {}
        )
    }
}
