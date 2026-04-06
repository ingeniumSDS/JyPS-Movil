package mx.edu.utez.jyps.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import mx.edu.utez.jyps.ui.screens.departmenthead.DepartmentHeadDashboardScreen
import mx.edu.utez.jyps.ui.screens.employee.EmployeeDashboardScreen
import mx.edu.utez.jyps.ui.screens.employee.EmployeeHistoryScreen
import mx.edu.utez.jyps.ui.screens.employee.ProfileScreen
import mx.edu.utez.jyps.ui.screens.employee.PassRequestScreen
import mx.edu.utez.jyps.ui.screens.employee.JustificationRequestScreen
import mx.edu.utez.jyps.ui.screens.security.ScannerScreen
import mx.edu.utez.jyps.viewmodel.AdminViewModel
import mx.edu.utez.jyps.viewmodel.DepartmentHeadViewModel
import mx.edu.utez.jyps.viewmodel.ForgotPasswordViewModel
import mx.edu.utez.jyps.viewmodel.LoginViewModel

/**
 * AppRoutes defines the navigation destinations for the application.
 */
sealed class AppRoutes(val route: String) {
    object Login : AppRoutes("login")
    object Home : AppRoutes("home")
    object EmployeeHome : AppRoutes("employee_home")
    object DeptHeadDashboard : AppRoutes("department_head_dashboard")
    object History : AppRoutes("history")
    object Profile : AppRoutes("profile")
    object SecurityScanner : AppRoutes("security_scanner")
    object ForgotPassword : AppRoutes("forgot_password")
    object PassRequest : AppRoutes("pass_request")
    object JustificationRequest : AppRoutes("justification_request")
    // Dept Head scoped employee screens with "Modo Empleado" banner
    object DeptHeadPassRequest : AppRoutes("dept_head_pass_request")
    object DeptHeadJustificationRequest : AppRoutes("dept_head_justification_request")
    object DeptHeadHistory : AppRoutes("dept_head_history")
    object DeptHeadProfile : AppRoutes("dept_head_profile")
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
    
    val (targetRoute, currentUser, currentUserEmail) = remember(sessionToken) {
        when (sessionToken) {
            "MOCK_SECURITY_TOKEN" -> Triple(AppRoutes.SecurityScanner.route, "María González Hernández", "m.gonzalez@utez.edu.mx")
            "MOCK_EMPLOYEE_TOKEN" -> Triple(AppRoutes.EmployeeHome.route, "Juan Pérez García", "juan.perez@utez.edu.mx")
            "MOCK_DEPT_HEAD_TOKEN" -> Triple(AppRoutes.DeptHeadDashboard.route, "Roberto Sánchez López", "roberto.sanchez@utez.edu.mx")
            null, "" -> Triple(AppRoutes.Login.route, "", "")
            else -> Triple(AppRoutes.Home.route, "Carlos Rodríguez Torres", "carlos.rodriguez@utez.edu.mx")
        }
    }

    LaunchedEffect(targetRoute) {
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
        
        // Employee Scope Dashboard
        composable(AppRoutes.EmployeeHome.route) { backStackEntry ->
            val savedStateHandle = backStackEntry.savedStateHandle
            val successMessage by savedStateHandle.getStateFlow<String?>("success_message", null).collectAsStateWithLifecycle()

            Box(modifier = Modifier.fillMaxSize()) {
                EmployeeDashboardScreen(
                    onLogoutClick = { loginViewModel.logout() },
                    onHistoryClick = { navController.navigate(AppRoutes.History.route) },
                    onProfileClick = { navController.navigate(AppRoutes.Profile.route) },
                    onRequestPassClick = { navController.navigate(AppRoutes.PassRequest.route) },
                    onRequestJustificationClick = { navController.navigate(AppRoutes.JustificationRequest.route) },
                    userName = currentUser,
                    userEmail = currentUserEmail
                )

                mx.edu.utez.jyps.ui.components.common.AppToast(
                    message = successMessage,
                    isVisible = successMessage != null,
                    onDismiss = { savedStateHandle["success_message"] = null },
                    type = mx.edu.utez.jyps.ui.components.common.ToastType.SUCCESS,
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }
        }

        // Pass Request
        composable(AppRoutes.PassRequest.route) {
            PassRequestScreen(
                onBackClick = { navController.navigateUp() },
                onSuccessSubmit = { msg ->
                    navController.previousBackStackEntry?.savedStateHandle?.set("success_message", msg)
                    navController.navigateUp()
                },
                userName = currentUser,
                userEmail = currentUserEmail
            )
        }
        
        // Justification Request
        composable(AppRoutes.JustificationRequest.route) {
            JustificationRequestScreen(
                onBackClick = { navController.navigateUp() },
                onSuccessSubmit = { msg ->
                    navController.previousBackStackEntry?.savedStateHandle?.set("success_message", msg)
                    navController.navigateUp()
                },
                userName = currentUser,
                userEmail = currentUserEmail
            )
        }

        // Employee History
        composable(AppRoutes.History.route) {
            val historyViewModel: mx.edu.utez.jyps.viewmodel.EmployeeHistoryViewModel = viewModel()
            EmployeeHistoryScreen(
                onLogoutClick = { loginViewModel.logout() },
                onHomeClick = { navController.navigate(AppRoutes.EmployeeHome.route) },
                onProfileClick = { navController.navigate(AppRoutes.Profile.route) },
                viewModel = historyViewModel,
                userName = currentUser
            )
        }

        // Employee Profile
        composable(AppRoutes.Profile.route) {
            ProfileScreen(
                onLogoutClick = { loginViewModel.logout() },
                onHomeClick = { navController.navigate(AppRoutes.EmployeeHome.route) },
                onHistoryClick = { navController.navigate(AppRoutes.History.route) },
                userName = currentUser
            )
        }
        
        // Security Guard Scope Dashboard
        composable(AppRoutes.SecurityScanner.route) {
            ScannerScreen(
                onLogoutClick = { loginViewModel.logout() }
            )
        }

        // Core App Generic Dashboard (Admin)
        composable(AppRoutes.Home.route) {
            val adminViewModel: AdminViewModel = viewModel()
            AdminDashboardScreen(
                viewModel = adminViewModel,
                onLogoutSuccess = {
                    loginViewModel.logout()
                }
            )
        }

        // Department Head Scope Dashboard
        composable(AppRoutes.DeptHeadDashboard.route) {
            val deptHeadViewModel: DepartmentHeadViewModel = viewModel()
            DepartmentHeadDashboardScreen(
                viewModel = deptHeadViewModel,
                onLogoutClick = { loginViewModel.logout() },
                onNavigate = { route ->
                    when (route) {
                        "department_head_dashboard" -> { /* already here */ }
                        "pass_request" -> navController.navigate(AppRoutes.DeptHeadPassRequest.route)
                        "justification_request" -> navController.navigate(AppRoutes.DeptHeadJustificationRequest.route)
                        "history" -> navController.navigate(AppRoutes.DeptHeadHistory.route)
                        "profile" -> navController.navigate(AppRoutes.DeptHeadProfile.route)
                        else -> { /* "dept_employees" — TODO: future screen */ }
                    }
                }
            )
        }

        // Dept Head → Employee Mode: Pass Request (with Modo Empleado banner)
        composable(AppRoutes.DeptHeadPassRequest.route) {
            PassRequestScreen(
                onBackClick = { navController.navigateUp() },
                onSuccessSubmit = { msg ->
                    navController.previousBackStackEntry?.savedStateHandle?.set("success_message", msg)
                    navController.navigateUp()
                },
                showEmployeeModeBanner = true,
                onReturnToRoleDashboard = {
                    navController.navigate(AppRoutes.DeptHeadDashboard.route) {
                        popUpTo(AppRoutes.DeptHeadDashboard.route) { inclusive = false }
                    }
                },
                userName = currentUser,
                userEmail = currentUserEmail
            )
        }

        // Dept Head → Employee Mode: Justification Request
        composable(AppRoutes.DeptHeadJustificationRequest.route) {
            JustificationRequestScreen(
                onBackClick = { navController.navigateUp() },
                onSuccessSubmit = { msg ->
                    navController.previousBackStackEntry?.savedStateHandle?.set("success_message", msg)
                    navController.navigateUp()
                },
                showEmployeeModeBanner = true,
                onReturnToRoleDashboard = {
                    navController.navigate(AppRoutes.DeptHeadDashboard.route) {
                        popUpTo(AppRoutes.DeptHeadDashboard.route) { inclusive = false }
                    }
                },
                userName = currentUser,
                userEmail = currentUserEmail
            )
        }

        // Dept Head → Employee Mode: History
        composable(AppRoutes.DeptHeadHistory.route) {
            val historyViewModel: mx.edu.utez.jyps.viewmodel.EmployeeHistoryViewModel = viewModel()
            EmployeeHistoryScreen(
                onLogoutClick = { loginViewModel.logout() },
                onHomeClick = { navController.navigate(AppRoutes.DeptHeadDashboard.route) },
                onProfileClick = { navController.navigate(AppRoutes.DeptHeadProfile.route) },
                viewModel = historyViewModel,
                showEmployeeModeBanner = true,
                onReturnToRoleDashboard = {
                    navController.navigate(AppRoutes.DeptHeadDashboard.route) {
                        popUpTo(AppRoutes.DeptHeadDashboard.route) { inclusive = false }
                    }
                },
                userName = currentUser
            )
        }

        // Dept Head → Employee Mode: Profile
        composable(AppRoutes.DeptHeadProfile.route) {
            ProfileScreen(
                onLogoutClick = { loginViewModel.logout() },
                onHomeClick = { navController.navigate(AppRoutes.DeptHeadDashboard.route) },
                onHistoryClick = { navController.navigate(AppRoutes.DeptHeadHistory.route) },
                showEmployeeModeBanner = true,
                onReturnToRoleDashboard = {
                    navController.navigate(AppRoutes.DeptHeadDashboard.route) {
                        popUpTo(AppRoutes.DeptHeadDashboard.route) { inclusive = false }
                    }
                },
                userName = currentUser
            )
        }
    }
}
