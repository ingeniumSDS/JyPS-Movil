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
    object ForgotPassword : AppRoutes("forgot_password")
}

/**
 * Connects the UI layers to the navigation graph.
 * 
 * Subscribes to the global `isLoggedIn` flow emitted by the auth repository. This ensures
 * robust state-driven routing: if the TokenManager clears the token (e.g. from an interceptor HTTP 401),
 * this host observes the emission and immediately sweeps the navigator back to the Login screen, 
 * destroying any backstack traces of the dashboard.
 * 
 * @param navController Controller managing internal stack and transitions.
 * @param loginViewModel Exposes the reactive authentication state.
 */
@Composable
fun NavigationHost(
    navController: NavHostController = rememberNavController(),
    loginViewModel: LoginViewModel = viewModel()
) {
    val isLoggedIn by loginViewModel.isLoggedIn.collectAsStateWithLifecycle()

    androidx.compose.runtime.LaunchedEffect(isLoggedIn) {
        if (!isLoggedIn) {
            if (navController.currentDestination?.route != AppRoutes.Login.route &&
                navController.currentDestination?.route != AppRoutes.ForgotPassword.route) {
                    
                navController.navigate(AppRoutes.Login.route) {
                    popUpTo(navController.graph.id) { inclusive = true }
                }
            }
        } else {
            if (navController.currentDestination?.route == AppRoutes.Login.route) {
                navController.navigate(AppRoutes.Home.route) {
                    popUpTo(AppRoutes.Login.route) { inclusive = true }
                }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = if (isLoggedIn) AppRoutes.Home.route else AppRoutes.Login.route
    ) {
        composable(AppRoutes.Login.route) {
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
