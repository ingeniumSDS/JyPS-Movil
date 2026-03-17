package mx.edu.utez.jyps.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import mx.edu.utez.jyps.ui.screens.ForgotPasswordScreen
import mx.edu.utez.jyps.ui.screens.LoginScreen
import mx.edu.utez.jyps.viewmodel.ForgotPasswordViewModel
import mx.edu.utez.jyps.viewmodel.LoginViewModel

/**
 * AppRoutes defines the navigation destinations for the application.
 */
sealed class AppRoutes(val route: String) {
    object Login : AppRoutes("login")
    object Home : AppRoutes("home")
    object ForgotPassword : AppRoutes("forgot_password")
}

/**
 * NavigationHost manages the navigation logic and screen composition.
 */
@Composable
fun NavigationHost(
    navController: NavHostController = rememberNavController(),
    startDestination: String = AppRoutes.Login.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(AppRoutes.Login.route) {
            val loginViewModel: LoginViewModel = viewModel()
            LoginScreen(
                viewModel = loginViewModel,
                onLoginSuccess = {
                    navController.navigate(AppRoutes.Home.route) {
                        popUpTo(AppRoutes.Login.route) { inclusive = true }
                    }
                },
                onForgotPasswordClick = {
                    navController.navigate(AppRoutes.ForgotPassword.route)
                }
            )
        }
        
        composable(AppRoutes.ForgotPassword.route) {
            val forgotPasswordViewModel: ForgotPasswordViewModel = viewModel()
            ForgotPasswordScreen(
                viewModel = forgotPasswordViewModel,
                onBackToLoginClick = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(AppRoutes.Home.route) {
            val adminViewModel: mx.edu.utez.jyps.viewmodel.AdminViewModel = viewModel()
            mx.edu.utez.jyps.ui.screens.admin.AdminDashboardScreen(
                viewModel = adminViewModel,
                onLogoutSuccess = {
                    navController.navigate(AppRoutes.Login.route) {
                        popUpTo(AppRoutes.Home.route) { inclusive = true }
                    }
                }
            )
        }
    }
}
