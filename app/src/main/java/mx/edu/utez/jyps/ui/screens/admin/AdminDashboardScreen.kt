package mx.edu.utez.jyps.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import mx.edu.utez.jyps.ui.components.navigation.AppNavigationDrawer
import mx.edu.utez.jyps.ui.components.navigation.AppTopBar
import mx.edu.utez.jyps.ui.components.navigation.adminMenuOptions
import mx.edu.utez.jyps.ui.components.dialogs.CreateUserDialog
import mx.edu.utez.jyps.ui.components.dialogs.EditUserDialog
import mx.edu.utez.jyps.ui.components.dialogs.UserDetailDialog
import mx.edu.utez.jyps.viewmodel.AdminViewModel
import mx.edu.utez.jyps.viewmodel.DepartmentManagementViewModel
import mx.edu.utez.jyps.ui.theme.JyPSTheme
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import mx.edu.utez.jyps.ui.components.dialogs.CreateDepartmentDialog
import mx.edu.utez.jyps.ui.components.dialogs.EditDepartmentDialog
import mx.edu.utez.jyps.ui.components.dialogs.ToggleDepartmentStatusDialogs

/**
 * Main wrapper screen for the Administrator dashboard.
 *
 * @param viewModel ViewModel tracking view state for the Admin role.
 * @param onLogoutSuccess Callback executed upon successful session logout.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    viewModel: AdminViewModel,
    deptViewModel: DepartmentManagementViewModel = viewModel(),
    onLogoutSuccess: () -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val selectedRoute by viewModel.selectedDrawerItem.collectAsStateWithLifecycle()
    val currentTitle = adminMenuOptions.find { it.route == selectedRoute }?.title ?: "Administrador"

    // Toast / Feedback — using Snackbar on top of everything
    val showToast by viewModel.showToast.collectAsStateWithLifecycle()
    val toastMessage by viewModel.toastMessage.collectAsStateWithLifecycle()
    val isProcessing by viewModel.isProcessing.collectAsStateWithLifecycle()

    LaunchedEffect(showToast) {
        if (showToast) {
            snackbarHostState.showSnackbar(toastMessage)
            viewModel.dismissToast()
        }
    }

    AppNavigationDrawer(
        drawerState = drawerState,
        menuItems = adminMenuOptions,
        currentRoute = selectedRoute,
        userFullName = "Carlos Rodríguez Torres",
        userEmail = "carlos.rodriguez@utez.edu.mx",
        roleTitle = "Administrador",
        onNavigateTo = { route ->
            viewModel.selectDrawerItem(route)
            coroutineScope.launch { drawerState.close() }
        },
        onLogout = {
            viewModel.onLogout()
            onLogoutSuccess()
        }
    ) {
        Scaffold(
            topBar = {
                AppTopBar(
                    title = "Sistema JyPS", // Consistent brand title instead of dynamic title
                    onMenuClick = { coroutineScope.launch { drawerState.open() } }
                )
            },
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState) { data ->
                    val isSuccess = toastMessage.contains("exitosamente", true)
                    Surface(
                        modifier = Modifier.padding(16.dp),
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSuccess) Color(0xFF28A745) else Color(0xFFDC3545),
                        shadowElevation = 8.dp
                    ) {
                        Text(
                            text = data.visuals.message,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color(0xFFF8F9FA))
            ) {
                when (selectedRoute) {
                    "admin_users" -> UserManagementContent(viewModel)
                    "admin_departments" -> {
                        val deptUiState by deptViewModel.uiState.collectAsStateWithLifecycle()
                        DepartmentManagementContent(
                            uiState = deptUiState,
                            onSearchQueryChange = deptViewModel::onSearchQueryChange,
                            onFilterChange = deptViewModel::onFilterChange,
                            onAddClick = deptViewModel::openCreate,
                            onEditClick = deptViewModel::openEdit,
                            onToggleStatusClick = deptViewModel::requestToggleStatus
                        )
                    }
                    else -> {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                            Text("Pantalla en Construcción: $selectedRoute")
                        }
                    }
                }
            }
        }

        // Dialogs
        CreateUserDialog(viewModel)
        EditUserDialog(viewModel)
        UserDetailDialog(viewModel)

        // Department Dialogs
        CreateDepartmentDialog(deptViewModel)
        EditDepartmentDialog(deptViewModel)
        ToggleDepartmentStatusDialogs(
            viewModel = deptViewModel,
            onManageEmployees = { viewModel.selectDrawerItem("admin_users") }
        )

        // Processing overlay
        if (isProcessing) {
            Surface(modifier = Modifier.fillMaxSize(), color = Color.Black.copy(alpha = 0.3f)) {
                Box(contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color.White)
                }
            }
        }
    }
}

/**
 * Preview for the Administrator dashboard boundary.
 */
@Preview(showBackground = true)
@Composable
fun AdminDashboardScreenPreview() {
    JyPSTheme {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Vista previa (Preview) no disponible nativamente para el Screen principal.")
        }
    }
}
