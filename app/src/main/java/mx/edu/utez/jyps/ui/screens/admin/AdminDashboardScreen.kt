package mx.edu.utez.jyps.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import mx.edu.utez.jyps.ui.components.navigation.AdminNavigationDrawer
import mx.edu.utez.jyps.ui.components.navigation.adminMenuOptions
import mx.edu.utez.jyps.viewmodel.AdminViewModel
import mx.edu.utez.jyps.ui.screens.admin.UserManagementContent

/**
 * Main wrapper screen for the Administrator dashboard,
 * handling the Drawer and the Top App Bar.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    viewModel: AdminViewModel,
    onLogoutSuccess: () -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    
    // Listen to the selected drawer item route
    val selectedRoute by viewModel.selectedDrawerItem.collectAsStateWithLifecycle()
    
    // Find title for current route
    val currentTitle = adminMenuOptions.find { it.route == selectedRoute }?.title ?: "Administrador"

    AdminNavigationDrawer(
        drawerState = drawerState,
        currentRoute = selectedRoute,
        onNavigateTo = { route ->
            viewModel.selectDrawerItem(route)
            coroutineScope.launch { drawerState.close() }
        },
        onLogout = {
            viewModel.onLogout()
            onLogoutSuccess()
        }
    ) {
        // Main Content Area
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(currentTitle) },
                    navigationIcon = {
                        IconButton(onClick = { coroutineScope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Abrir Menú")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.White,
                        titleContentColor = Color(0xFF0F2C59),
                        navigationIconContentColor = Color(0xFF0F2C59)
                    )
                )
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color(0xFFF8F9FA))
            ) {
                // Here we render the specific screen based on the route
                when (selectedRoute) {
                    "admin_users" -> {
                        UserManagementContent(viewModel)
                    }
                    else -> {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                            Text("Pantalla en Construcción: $selectedRoute")
                        }
                    }
                }
            }
        }

        // --- DIALOGS ---
        CreateUserDialog(viewModel)
        UserDetailDialog(viewModel)

        // --- FEEDBACK / TOAST ---
        val showToast by viewModel.showToast.collectAsStateWithLifecycle()
        val toastMessage by viewModel.toastMessage.collectAsStateWithLifecycle()
        val isProcessing by viewModel.isProcessing.collectAsStateWithLifecycle()

        if (showToast) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 32.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                Surface(
                    modifier = Modifier
                        .size(width = 341.dp, height = 53.dp)
                        .clickable { viewModel.dismissToast() },
                    shape = RoundedCornerShape(8.dp),
                    color = Color.White,
                    shadowElevation = 8.dp,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x1A000000))
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = toastMessage,
                            fontSize = 14.sp,
                            color = if (toastMessage.contains("exitosamente", true)) Color(0xFF28A745) else Color(0xFFFB2C36),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        if (isProcessing) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Color.Black.copy(alpha = 0.3f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    androidx.compose.material3.CircularProgressIndicator(color = Color.White)
                }
            }
        }
    }
}
