package mx.edu.utez.jyps.ui.screens.departmenthead

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import mx.edu.utez.jyps.ui.components.departmenthead.CreateEmployeeDialog
import mx.edu.utez.jyps.ui.components.departmenthead.EditEmployeeDialog
import mx.edu.utez.jyps.ui.components.departmenthead.EmployeeCard
import mx.edu.utez.jyps.ui.components.navigation.AppNavigationDrawer
import mx.edu.utez.jyps.ui.components.navigation.AppTopBar
import mx.edu.utez.jyps.ui.components.navigation.deptHeadMenuOptions
import mx.edu.utez.jyps.viewmodel.EmployeeManagementViewModel

/**
 * Final redesigned EmployeeManagementScreen matching Figma strictly.
 * 
 * @param viewModel State management.
 * @param onLogoutClick Exit session.
 * @param onNavigate Navigation drawer callback.
 * @param userName Injected dynamic user name.
 * @param userEmail Injected dynamic user email.
 */
@Composable
fun EmployeeManagementScreen(
    viewModel: EmployeeManagementViewModel = viewModel(),
    onLogoutClick: () -> Unit = {},
    onNavigate: (String) -> Unit = {},
    userName: String = "Jefe de Departamento",
    userEmail: String = "jefe@utez.edu.mx"
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    AppNavigationDrawer(
        drawerState = drawerState,
        menuItems = deptHeadMenuOptions,
        currentRoute = "dept_employees",
        userFullName = userName,
        userEmail = userEmail,
        roleTitle = "Jefe de Departamento",
        onNavigateTo = { route ->
            scope.launch { drawerState.close() }
            onNavigate(route)
        },
        onLogout = onLogoutClick
    ) {
        Scaffold(
            topBar = {
                AppTopBar(onMenuClick = { scope.launch { drawerState.open() } })
            },
            containerColor = Color.White
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(vertical = 24.dp)
            ) {
                // Titles
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "Gestión de Empleados",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F2C59)
                        )
                        Text(
                            text = "Administra los empleados asignados a tu área",
                            fontSize = 14.sp,
                            color = Color(0xFF6A7282)
                        )
                    }
                }

                // Create Button (Green, Huge)
                item {
                    Button(
                        onClick = { viewModel.onCreateClick() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF28A745))
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Crear Empleado",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                // Total Stats Card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Surface(
                                modifier = Modifier.size(48.dp),
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFF1F3F5)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Group,
                                        contentDescription = null,
                                        tint = Color(0xFF0F2C59)
                                    )
                                }
                            }
                            Column {
                                Text(
                                    text = "Total Empleados",
                                    fontSize = 12.sp,
                                    color = Color(0xFF6A7282)
                                )
                                Text(
                                    text = uiState.employees.size.toString(),
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F2C59)
                                )
                            }
                        }
                    }
                }

                // Search Bar
                item {
                    OutlinedTextField(
                        value = uiState.searchQuery,
                        onValueChange = { viewModel.onSearchQueryChanged(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        placeholder = { 
                            Text(
                                "Buscar por nombre, email o departa...",
                                fontSize = 14.sp,
                                color = Color(0xFF6A7282)
                            ) 
                        },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF6A7282)) },
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFD1D5DC),
                            unfocusedBorderColor = Color(0xFFD1D5DC),
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        ),
                        singleLine = true
                    )
                }

                // Employee Cards
                items(uiState.filteredEmployees, key = { it.id }) { employee ->
                    EmployeeCard(
                        employee = employee,
                        onEditClick = { viewModel.onEditClick(employee) }
                    )
                }
            }
        }
    }

    // Dialogs
    if (uiState.showCreateDialog) {
        CreateEmployeeDialog(
            onDismiss = { viewModel.onDismissDialogs() },
            onConfirm = { name, email, phone, empId, pos, dept ->
                viewModel.addEmployee(name, email, phone, empId, pos, dept)
            }
        )
    }

    uiState.selectedEmployee?.let { employee ->
        EditEmployeeDialog(
            employee = employee,
            onDismiss = { viewModel.onDismissDialogs() },
            onConfirm = { updatedEmployee ->
                viewModel.updateEmployee(updatedEmployee)
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun EmployeeManagementPreview() {
    EmployeeManagementScreen()
}
