package mx.edu.utez.jyps.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import mx.edu.utez.jyps.ui.screens.ForgotPasswordScreen
import mx.edu.utez.jyps.ui.screens.LoginScreen
import mx.edu.utez.jyps.ui.screens.admin.AdminDashboardScreen
import mx.edu.utez.jyps.viewmodel.AdminViewModel
import mx.edu.utez.jyps.viewmodel.ForgotPasswordViewModel
import mx.edu.utez.jyps.viewmodel.LoginViewModel

/**
 * AppRoutes defines the navigation destinations for the application.
 */
sealed class AppRoutes(val route: String) {
    object Login : AppRoutes("login")
    object Home : AppRoutes("home")
    object SecurityScanner : AppRoutes("security_scanner")
    object ForgotPassword : AppRoutes("forgot_password")
}

/**
 * Connects the UI layers to the navigation graph.
 * 
 * Subscribes to the global `sessionToken` flow emitted by the auth repository. This ensures
 * robust state-driven routing with dynamic role-based entry point mapping.
 * 
 * @param navController Controller managing internal stack and transitions.
 * @param loginViewModel Exposes the reactive authentication state.
 */
@Composable
fun NavigationHost(
    navController: NavHostController = rememberNavController(),
    loginViewModel: LoginViewModel = viewModel()
) {
    val sessionToken by loginViewModel.sessionToken.collectAsStateWithLifecycle()
    
    val targetRoute = remember(sessionToken) {
        when (sessionToken) {
            "MOCK_SECURITY_TOKEN" -> AppRoutes.SecurityScanner.route
            null, "" -> AppRoutes.Login.route
            else -> AppRoutes.Home.route
        }
    }

    androidx.compose.runtime.LaunchedEffect(targetRoute) {
        if (targetRoute == AppRoutes.Login.route) {
            if (navController.currentDestination?.route != AppRoutes.Login.route &&
                navController.currentDestination?.route != AppRoutes.ForgotPassword.route) {
                    
                navController.navigate(AppRoutes.Login.route) {
                    popUpTo(navController.graph.id) { inclusive = true }
                }
            }
        } else {
            if (navController.currentDestination?.route == AppRoutes.Login.route) {
                navController.navigate(targetRoute) {
                    popUpTo(AppRoutes.Login.route) { inclusive = true }
                }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = if (targetRoute != AppRoutes.Login.route) targetRoute else AppRoutes.Login.route
    ) {
        composable(AppRoutes.Login.route) {
            LoginScreen(
                viewModel = loginViewModel,
                onLoginSuccess = {
                    navController.navigate(targetRoute) {
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
        
        // Security Guard Scope Dashboard
        composable(AppRoutes.SecurityScanner.route) {
            mx.edu.utez.jyps.ui.screens.security.ScannerScreen()
        }

        // Core App Generic Dashboard
        composable(AppRoutes.Home.route) {
            val adminViewModel: AdminViewModel = viewModel()
            AdminDashboardScreen(
                viewModel = adminViewModel,
                onLogoutSuccess = {
                    loginViewModel.logout() // Use auth repo global logout
                }
            )
        }
    }
}
