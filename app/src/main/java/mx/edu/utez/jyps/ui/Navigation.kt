package mx.edu.utez.jyps.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.graphics.Color
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
import mx.edu.utez.jyps.ui.screens.departmenthead.EmployeeManagementScreen
import mx.edu.utez.jyps.ui.screens.security.ScannerScreen
import mx.edu.utez.jyps.viewmodel.AdminViewModel
import mx.edu.utez.jyps.viewmodel.DepartmentHeadViewModel
import mx.edu.utez.jyps.viewmodel.EmployeeHistoryViewModel
import mx.edu.utez.jyps.viewmodel.EmployeeManagementViewModel
import mx.edu.utez.jyps.viewmodel.ForgotPasswordViewModel
import mx.edu.utez.jyps.viewmodel.AuthViewModel
import mx.edu.utez.jyps.viewmodel.LoginViewModel
import mx.edu.utez.jyps.ui.components.common.AppToast
import mx.edu.utez.jyps.ui.components.common.ToastType

/**
 * AppRoutes defines the immutable navigation destinations for the application.
 * Centralizes route string constants to prevent hardcoded navigation errors.
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
    object DeptHeadEmployees : AppRoutes("dept_head_employees")
    // Admin scoped employee screens with "Modo Empleado" banner
    object AdminPassRequest : AppRoutes("admin_pass")
    object AdminJustificationRequest : AppRoutes("admin_excuse")
    object AdminHistory : AppRoutes("admin_history")
    object AdminProfile : AppRoutes("admin_profile")
    object AdminEmployeeHome : AppRoutes("admin_employee_home")
    object DeptHeadEmployeeHome : AppRoutes("dept_head_employee_home")
}

/**
 * Orchestrates the global navigation graph and security-driven routing.
 * 
 * Subscribes to identity and authorization flows emitted by the authentication layer. 
 * Implements a secure grace period while roles are synchronized from local storage 
 * to prevent unauthorized access or flickering during session initialization.
 * 
 * @param navController Controller managing internal stack and transitions.
 * @param loginViewModel Exposes the reactive authentication state.
 * @param authViewModel Main controller for shared identity state (Token, Roles, Profile).
 */
@Composable
fun NavigationHost(
    navController: NavHostController = rememberNavController(),
    loginViewModel: LoginViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel()
) {
    val sessionToken by authViewModel.isLoggedIn.collectAsStateWithLifecycle()
    val roles by authViewModel.currentRoles.collectAsStateWithLifecycle()
    val currentUser by authViewModel.userName.collectAsStateWithLifecycle()
    val currentUserEmail by authViewModel.userEmail.collectAsStateWithLifecycle()
    val currentUserPhone by authViewModel.userPhone.collectAsStateWithLifecycle()
    val currentUserId by authViewModel.userId.collectAsStateWithLifecycle()

    // SECURE NAVIGATION LOGIC: Derived from unified session state
    val targetRoute = remember(sessionToken, roles) {
        when {
            // Case 1: No session at all -> Always Login
            !sessionToken -> AppRoutes.Login.route

            // Case 2: Session exists but roles are still loading from disk -> WAIT
            // We return an empty string to signify "Indeterminate State"
            roles.isEmpty() -> ""

            // Case 3: Full session resolved -> Navigate based on role
            else -> when {
                roles.contains("ADMINISTRADOR") -> AppRoutes.Home.route
                roles.contains("JEFE_DE_DEPARTAMENTO") -> AppRoutes.DeptHeadDashboard.route
                roles.contains("GUARDIA") -> AppRoutes.SecurityScanner.route
                roles.contains("EMPLEADO") -> AppRoutes.EmployeeHome.route
                else -> AppRoutes.Login.route
            }
        }
    }

    // Current Role display name
    val currentRole = roles.firstOrNull() ?: ""

    // Initial redirection and session maintenance
    LaunchedEffect(sessionToken, targetRoute, roles) {
        // IMPORTANT: If we have a token but roles aren't ready, DO NOT navigate yet.
        // If targetRoute is empty string, we are in the "splash/loading" grace period.
        if (targetRoute.isEmpty()) return@LaunchedEffect

        val currentRoute = navController.currentDestination?.route
        if (currentRoute != targetRoute) {
            navController.navigate(targetRoute) {
                popUpTo(0) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF8F9FA))) {
        if (targetRoute.isEmpty()) {
            // Smooth splash transition while session is syncing
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color(0xFF0D6EFD))
            }
        } else {
            NavHost(
                navController = navController,
                startDestination = targetRoute,
                modifier = Modifier.fillMaxSize()
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
                    val successMessage by savedStateHandle.getStateFlow<String?>(
                        "success_message",
                        null
                    ).collectAsStateWithLifecycle()

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

                        AppToast(
                            message = successMessage,
                            isVisible = successMessage != null,
                            onDismiss = { savedStateHandle["success_message"] = null },
                            type = ToastType.SUCCESS,
                            modifier = Modifier.align(Alignment.BottomCenter)
                        )
                    }
                }

                // Pass Request
                composable(AppRoutes.PassRequest.route) {
                    PassRequestScreen(
                        onBackClick = { navController.navigateUp() },
                        onSuccessSubmit = { msg ->
                            navController.previousBackStackEntry?.savedStateHandle?.set(
                                "success_message",
                                msg
                            )
                            navController.navigateUp()
                        },
                        userName = currentUser,
                        userEmail = currentUserEmail,
                        userId = currentUserId
                    )
                }

                // Justification Request
                composable(AppRoutes.JustificationRequest.route) {
                    JustificationRequestScreen(
                        onBackClick = { navController.navigateUp() },
                        onSuccessSubmit = { msg ->
                            navController.previousBackStackEntry?.savedStateHandle?.set(
                                "success_message",
                                msg
                            )
                            navController.navigateUp()
                        },
                        userName = currentUser,
                        userEmail = currentUserEmail
                    )
                }

                // Employee History
                composable(AppRoutes.History.route) {
                    val historyViewModel: EmployeeHistoryViewModel = viewModel()
                    EmployeeHistoryScreen(
                        onLogoutClick = { loginViewModel.logout() },
                        onHomeClick = { navController.navigate(AppRoutes.EmployeeHome.route) },
                        onProfileClick = { navController.navigate(AppRoutes.Profile.route) },
                        viewModel = historyViewModel,
                        userName = currentUser,
                        userEmail = currentUserEmail
                    )
                }

                // Employee Profile
                composable(AppRoutes.Profile.route) {
                    ProfileScreen(
                        onLogoutClick = { loginViewModel.logout() },
                        onHomeClick = { navController.navigate(AppRoutes.EmployeeHome.route) },
                        onHistoryClick = { navController.navigate(AppRoutes.History.route) },
                        userName = currentUser,
                        userEmail = currentUserEmail,
                        userPhone = currentUserPhone,
                        roleTitle = currentRole
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
                        },
                        onNavigateToEmployeeFunction = { route ->
                            navController.navigate(route)
                        },
                        userName = currentUser,
                        userEmail = currentUserEmail
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
                                "department_head_dashboard" -> { /* already here */
                                }

                                "pass_request" -> navController.navigate(AppRoutes.DeptHeadPassRequest.route)
                                "justification_request" -> navController.navigate(AppRoutes.DeptHeadJustificationRequest.route)
                                "history" -> navController.navigate(AppRoutes.DeptHeadHistory.route)
                                "profile" -> navController.navigate(AppRoutes.DeptHeadProfile.route)
                                "dept_employees" -> navController.navigate(AppRoutes.DeptHeadEmployees.route)
                                else -> { /* Unknown route */
                                }
                            }
                        },
                        userName = currentUser,
                        userEmail = currentUserEmail
                    )
                }

                // Department Head: Employee Management
                composable(AppRoutes.DeptHeadEmployees.route) {
                    val employeeViewModel: EmployeeManagementViewModel = viewModel()
                    EmployeeManagementScreen(
                        viewModel = employeeViewModel,
                        onLogoutClick = { loginViewModel.logout() },
                        onNavigate = { route ->
                            when (route) {
                                "department_head_dashboard" -> navController.navigate(AppRoutes.DeptHeadDashboard.route) {
                                    popUpTo(AppRoutes.DeptHeadDashboard.route) { inclusive = false }
                                }

                                "pass_request" -> navController.navigate(AppRoutes.DeptHeadPassRequest.route)
                                "justification_request" -> navController.navigate(AppRoutes.DeptHeadJustificationRequest.route)
                                "history" -> navController.navigate(AppRoutes.DeptHeadHistory.route)
                                "profile" -> navController.navigate(AppRoutes.DeptHeadProfile.route)
                                else -> { /* Already here */
                                }
                            }
                        },
                        userName = currentUser,
                        userEmail = currentUserEmail
                    )
                }

                // Dept Head → Employee Mode: Pass Request (with banner)
                composable(AppRoutes.DeptHeadPassRequest.route) {
                    PassRequestScreen(
                        onBackClick = { navController.navigateUp() },
                        onSuccessSubmit = { msg ->
                            navController.previousBackStackEntry?.savedStateHandle?.set(
                                "success_message",
                                msg
                            )
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

                // Dept Head → Employee Mode: Home (with banner)
                composable(AppRoutes.DeptHeadEmployeeHome.route) {
                    EmployeeDashboardScreen(
                        onLogoutClick = { loginViewModel.logout() },
                        onHistoryClick = { navController.navigate(AppRoutes.DeptHeadHistory.route) },
                        onProfileClick = { navController.navigate(AppRoutes.DeptHeadProfile.route) },
                        onRequestPassClick = { navController.navigate(AppRoutes.DeptHeadPassRequest.route) },
                        onRequestJustificationClick = { navController.navigate(AppRoutes.DeptHeadJustificationRequest.route) },
                        userName = currentUser,
                        userEmail = currentUserEmail,
                        showEmployeeModeBanner = true,
                        onReturnToRoleDashboard = {
                            navController.navigate(AppRoutes.DeptHeadDashboard.route) {
                                popUpTo(AppRoutes.DeptHeadDashboard.route) { inclusive = false }
                            }
                        }
                    )
                }

                // Dept Head → Employee Mode: Justification Request
                composable(AppRoutes.DeptHeadJustificationRequest.route) {
                    JustificationRequestScreen(
                        onBackClick = { navController.navigateUp() },
                        onSuccessSubmit = { msg ->
                            navController.previousBackStackEntry?.savedStateHandle?.set(
                                "success_message",
                                msg
                            )
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
                    val historyViewModel: EmployeeHistoryViewModel = viewModel()
                    EmployeeHistoryScreen(
                        onLogoutClick = { loginViewModel.logout() },
                        onHomeClick = { navController.navigate(AppRoutes.DeptHeadEmployeeHome.route) },
                        onProfileClick = { navController.navigate(AppRoutes.DeptHeadProfile.route) },
                        viewModel = historyViewModel,
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

                // Dept Head → Employee Mode: Profile
                composable(AppRoutes.DeptHeadProfile.route) {
                    ProfileScreen(
                        onLogoutClick = { loginViewModel.logout() },
                        onHomeClick = { navController.navigate(AppRoutes.DeptHeadEmployeeHome.route) },
                        onHistoryClick = { navController.navigate(AppRoutes.DeptHeadHistory.route) },
                        showEmployeeModeBanner = true,
                        onReturnToRoleDashboard = {
                            navController.navigate(AppRoutes.DeptHeadDashboard.route) {
                                popUpTo(AppRoutes.DeptHeadDashboard.route) { inclusive = false }
                            }
                        },
                        userName = currentUser,
                        userEmail = currentUserEmail,
                        userPhone = currentUserPhone,
                        roleTitle = currentRole
                    )
                }
                // Admin → Employee Mode: Pass Request
                composable(AppRoutes.AdminPassRequest.route) {
                    PassRequestScreen(
                        onBackClick = { navController.navigateUp() },
                        onSuccessSubmit = { msg ->
                            navController.previousBackStackEntry?.savedStateHandle?.set(
                                "success_message",
                                msg
                            )
                            navController.navigateUp()
                        },
                        showEmployeeModeBanner = true,
                        onReturnToRoleDashboard = {
                            navController.navigate(AppRoutes.Home.route) {
                                popUpTo(AppRoutes.Home.route) { inclusive = false }
                            }
                        },
                        userName = currentUser,
                        userEmail = currentUserEmail
                    )
                }

                // Admin → Employee Mode: Home (with banner)
                composable(AppRoutes.AdminEmployeeHome.route) {
                    EmployeeDashboardScreen(
                        onLogoutClick = { loginViewModel.logout() },
                        onHistoryClick = { navController.navigate(AppRoutes.AdminHistory.route) },
                        onProfileClick = { navController.navigate(AppRoutes.AdminProfile.route) },
                        onRequestPassClick = { navController.navigate(AppRoutes.AdminPassRequest.route) },
                        onRequestJustificationClick = { navController.navigate(AppRoutes.AdminJustificationRequest.route) },
                        userName = currentUser,
                        userEmail = currentUserEmail,
                        showEmployeeModeBanner = true,
                        onReturnToRoleDashboard = {
                            navController.navigate(AppRoutes.Home.route) {
                                popUpTo(AppRoutes.Home.route) { inclusive = false }
                            }
                        }
                    )
                }

                // Admin → Employee Mode: Justification Request
                composable(AppRoutes.AdminJustificationRequest.route) {
                    JustificationRequestScreen(
                        onBackClick = { navController.navigateUp() },
                        onSuccessSubmit = { msg ->
                            navController.previousBackStackEntry?.savedStateHandle?.set(
                                "success_message",
                                msg
                            )
                            navController.navigateUp()
                        },
                        showEmployeeModeBanner = true,
                        onReturnToRoleDashboard = {
                            navController.navigate(AppRoutes.Home.route) {
                                popUpTo(AppRoutes.Home.route) { inclusive = false }
                            }
                        },
                        userName = currentUser,
                        userEmail = currentUserEmail
                    )
                }

                // Admin → Employee Mode: History
                composable(AppRoutes.AdminHistory.route) {
                    val historyViewModel: EmployeeHistoryViewModel = viewModel()
                    EmployeeHistoryScreen(
                        onLogoutClick = { loginViewModel.logout() },
                        onHomeClick = { navController.navigate(AppRoutes.AdminEmployeeHome.route) },
                        onProfileClick = { navController.navigate(AppRoutes.AdminProfile.route) },
                        viewModel = historyViewModel,
                        showEmployeeModeBanner = true,
                        onReturnToRoleDashboard = {
                            navController.navigate(AppRoutes.Home.route) {
                                popUpTo(AppRoutes.Home.route) { inclusive = false }
                            }
                        },
                        userName = currentUser,
                        userEmail = currentUserEmail
                    )
                }

                // Admin → Employee Mode: Profile
                composable(AppRoutes.AdminProfile.route) {
                    ProfileScreen(
                        onLogoutClick = { loginViewModel.logout() },
                        onHomeClick = { navController.navigate(AppRoutes.AdminEmployeeHome.route) },
                        onHistoryClick = { navController.navigate(AppRoutes.AdminHistory.route) },
                        showEmployeeModeBanner = true,
                        onReturnToRoleDashboard = {
                            navController.navigate(AppRoutes.Home.route) {
                                popUpTo(AppRoutes.Home.route) { inclusive = false }
                            }
                        },
                        userName = currentUser,
                        userEmail = currentUserEmail,
                        userPhone = currentUserPhone,
                        roleTitle = currentRole
                    )
                }
            }
        }
    }
}