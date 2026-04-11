package mx.edu.utez.jyps.ui.components.login

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mx.edu.utez.jyps.ui.components.buttons.PrimaryButton
import mx.edu.utez.jyps.ui.components.inputs.AppTextField
import mx.edu.utez.jyps.ui.components.modals.PrivacyPolicyModal
import mx.edu.utez.jyps.ui.components.modals.SecurityNoticeModal
import mx.edu.utez.jyps.ui.components.texts.AppLogo
import mx.edu.utez.jyps.ui.theme.JyPSTheme
import mx.edu.utez.jyps.viewmodel.LoginUiState

/**
 * Stateless skeleton for the Login screen interface.
 * Decouples the UI structure from the business logic, facilitating isolated previews and testing.
 *
 * @param uiState Immutable view state containing credentials and security statuses.
 * @param onEmailChange Callback triggered on every keystroke in the email input field.
 * @param onPasswordChange Callback triggered on every keystroke in the password input field.
 * @param onLoginClick Primary action to initiate the backend authentication handshake.
 * @param onForgotPasswordClick Navigation action to transition to the recovery flow.
 * @param onReturnToLogin Action to clear security lockouts and return to the credential input step.
 */
@Composable
fun LoginContent(
    uiState: LoginUiState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    onReturnToLogin: () -> Unit = {}
) {
    var showSecurityModal by remember { mutableStateOf(false) }
    var showPrivacyModal by remember { mutableStateOf(false) }

    if (showSecurityModal) {
        SecurityNoticeModal(onDismissRequest = { showSecurityModal = false })
    }

    if (showPrivacyModal) {
        PrivacyPolicyModal(onDismissRequest = { showPrivacyModal = false })
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .verticalScroll(scrollState)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        AppLogo()
        
        Spacer(modifier = Modifier.height(32.dp))

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
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                if (uiState.isLockedOut || uiState.isAccountBlocked) {
                    LockedOutView(
                        isServerSide = uiState.isAccountBlocked,
                        serverMessage = if (uiState.isAccountBlocked) uiState.errorMessage else null,
                        onReturnToLogin = onReturnToLogin
                    )
                    
                    if (!uiState.isAccountBlocked) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.7.dp, Color(0xFF0F2C59), RoundedCornerShape(8.dp))
                                    .padding(vertical = 12.dp)
                                    .clickable { onForgotPasswordClick() },
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "¿Olvidaste tu contraseña?",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF0F2C59)
                                )
                            }
    
                            Text(
                                text = "El acceso se restablecerá automáticamente cuando el contador llegue a cero.",
                                fontSize = 12.sp,
                                color = Color(0xFF6A7282),
                                textAlign = TextAlign.Center,
                                lineHeight = 16.sp
                            )
    
                            LoginFooterLinks()
                        }
                    }
                } else {
                    AppTextField(
                        value = uiState.email,
                        onValueChange = onEmailChange,
                        label = "Correo Electrónico",
                        placeholder = "tu@email.com"
                    )

                    AppTextField(
                        value = uiState.password,
                        onValueChange = onPasswordChange,
                        label = "Contraseña",
                        placeholder = "••••••••",
                        isPassword = true
                    )

                    if (uiState.errorMessage != null) {
                        Text(
                            text = uiState.errorMessage,
                            color = Color(0xFFDC3545),
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFFEF2F2), RoundedCornerShape(8.dp))
                                .border(1.dp, Color(0xFFFFC9C9), RoundedCornerShape(8.dp))
                                .padding(12.dp)
                        )
                    }

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        Text(
                            text = "¿Olvidaste tu contraseña?",
                            fontSize = 14.sp,
                            color = Color(0xFF0F2C59),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.clickable { onForgotPasswordClick() }
                        )

                        PrimaryButton(
                            text = if (uiState.isLoading) "Cargando..." else "Ingresar",
                            onClick = onLoginClick,
                            enabled = !uiState.isLoading
                        )

                        LoginFooterLinks()
                    }
                }
            }
        }

        if (!uiState.isLockedOut) {
            Spacer(modifier = Modifier.height(24.dp))
            TestUsersBox()
        }

        Spacer(modifier = Modifier.height(24.dp))

        InfoNoticeCards(
            onSecurityClick = { showSecurityModal = true },
            onPrivacyClick = { showPrivacyModal = true }
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "© 2026 UTEZ. Sistema JyPS v1.0.0",
            fontSize = 12.sp,
            color = Color(0xFF6A7282),
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true, device = "spec:width=411dp,height=891dp")
@Composable
fun LoginContentPreview() {
    JyPSTheme {
        LoginContent(
            uiState = LoginUiState(email = "test@example.com"),
            onEmailChange = {},
            onPasswordChange = {},
            onLoginClick = {},
            onForgotPasswordClick = {}
        )
    }
}
