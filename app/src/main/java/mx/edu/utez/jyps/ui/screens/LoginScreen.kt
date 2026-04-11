package mx.edu.utez.jyps.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import mx.edu.utez.jyps.ui.components.login.LoginContent
import mx.edu.utez.jyps.ui.theme.JyPSTheme
import mx.edu.utez.jyps.viewmodel.LoginUiState
import mx.edu.utez.jyps.viewmodel.LoginViewModel

/**
 * LoginScreen is the high-level entry point that connects the authentication UI with the business logic.
 * Orchestrates the transition to success flows upon successful credential validation.
 *
 * @param viewModel The ViewModel handling the authentication state and network handshakes.
 * @param onLoginSuccess Callback fired when the identity provider confirms a valid session.
 * @param onForgotPasswordClick Navigation callback to transition the user to the recovery flow.
 */
@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onLoginSuccess: () -> Unit,
    onForgotPasswordClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.isLoginSuccessful) {
        if (uiState.isLoginSuccessful) {
            onLoginSuccess()
        }
    }

    LoginContent(
        uiState = uiState,
        onEmailChange = viewModel::onEmailChange,
        onPasswordChange = viewModel::onPasswordChange,
        onLoginClick = viewModel::login,
        onForgotPasswordClick = onForgotPasswordClick,
        onReturnToLogin = viewModel::resetBlockedState
    )
}

@Preview(showBackground = true, device = "spec:width=411dp,height=891dp")
@Composable
fun LoginScreenPreview() {
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
