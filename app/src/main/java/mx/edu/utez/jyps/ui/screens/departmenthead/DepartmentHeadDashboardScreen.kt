package mx.edu.utez.jyps.ui.screens.departmenthead

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DoNotDisturbOn
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Pending
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
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
import mx.edu.utez.jyps.data.model.RequestType
import mx.edu.utez.jyps.ui.components.departmenthead.AlertBannerCard
import mx.edu.utez.jyps.ui.components.departmenthead.FilterChipRow
import mx.edu.utez.jyps.ui.components.departmenthead.FilterOption
import mx.edu.utez.jyps.ui.components.departmenthead.JustificationDetailDialog
import mx.edu.utez.jyps.ui.components.departmenthead.PassDetailDialog
import mx.edu.utez.jyps.ui.components.departmenthead.RejectDialog
import mx.edu.utez.jyps.ui.components.departmenthead.RequestCard
import mx.edu.utez.jyps.ui.components.departmenthead.StatCard
import mx.edu.utez.jyps.ui.components.navigation.AppNavigationDrawer
import mx.edu.utez.jyps.ui.components.navigation.AppTopBar
import mx.edu.utez.jyps.ui.components.navigation.deptHeadMenuOptions
import mx.edu.utez.jyps.viewmodel.DepartmentHeadViewModel
import mx.edu.utez.jyps.viewmodel.RequestFilter

/**
 * Main dashboard screen for the Department Head role.
 * Displays statistics cards, a pending-alert banner, filterable request list,
 * and opens detail/reject dialogs on card tap.
 *
 * @param viewModel ViewModel managing the dashboard state.
 * @param onLogoutClick Callback for session termination.
 * @param onNavigate Callback handling drawer menu navigation with the route string.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DepartmentHeadDashboardScreen(
    viewModel: DepartmentHeadViewModel = viewModel(),
    onLogoutClick: () -> Unit = {},
    onNavigate: (route: String) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Detail dialog
    uiState.selectedItem?.let { item ->
        // Only show reject sub-dialog when explicitly requested
        if (uiState.showRejectDialog) {
            RejectDialog(
                employeeName = item.employeeName,
                onDismiss = { viewModel.dismissRejectDialog() },
                onConfirm = { reason -> viewModel.rejectRequest(item.id, reason) }
            )
        } else {
            // Show the appropriate detail dialog based on request type
            when (item.requestType) {
                RequestType.PASS -> PassDetailDialog(
                    item = item,
                    onDismiss = { viewModel.dismissDetailDialog() },
                    onApprove = { viewModel.approveRequest(item.id) },
                    onReject = { viewModel.openRejectDialog() }
                )
                RequestType.JUSTIFICATION -> JustificationDetailDialog(
                    item = item,
                    onDismiss = { viewModel.dismissDetailDialog() },
                    onApprove = { viewModel.approveRequest(item.id) },
                    onReject = { viewModel.openRejectDialog() }
                )
            }
        }
    }

    AppNavigationDrawer(
        drawerState = drawerState,
        menuItems = deptHeadMenuOptions,
        currentRoute = "department_head_dashboard",
        userFullName = "Roberto Sánchez López",
        userEmail = "roberto.sanchez@utez.edu.mx",
        roleTitle = "Jefe de Departamento",
        onNavigateTo = { route ->
            scope.launch { drawerState.close() }
            onNavigate(route)
        },
        onLogout = {
            scope.launch { drawerState.close() }
            onLogoutClick()
        }
    ) {
        Scaffold(
            topBar = {
                AppTopBar(
                    onMenuClick = { scope.launch { drawerState.open() } }
                )
            },
            containerColor = Color(0xFFF8F9FA)
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Header
                item {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Dashboard",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1F2937)
                    )
                    Text(
                        text = "Vista general del sistema de pases y justificantes",
                        fontSize = 14.sp,
                        color = Color(0xFF6B7280)
                    )
                }

                // Stats cards — 2x2 grid
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            StatCard(
                                label = "Total Solicitudes",
                                count = uiState.totalCount,
                                icon = Icons.Default.Assessment,
                                iconBgColor = Color(0xFFE8F0FE),
                                iconTint = Color(0xFF0F2C59)
                            )
                            StatCard(
                                label = "Aprobadas",
                                count = uiState.approvedCount,
                                icon = Icons.Default.CheckCircle,
                                iconBgColor = Color(0xFFDCFCE7),
                                iconTint = Color(0xFF016630)
                            )
                        }
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            StatCard(
                                label = "Pendientes",
                                count = uiState.pendingCount,
                                icon = Icons.Default.Pending,
                                iconBgColor = Color(0xFFFEF9C2),
                                iconTint = Color(0xFF894B00)
                            )
                            StatCard(
                                label = "Rechazadas",
                                count = uiState.rejectedCount,
                                icon = Icons.Default.DoNotDisturbOn,
                                iconBgColor = Color(0xFFFFE2E2),
                                iconTint = Color(0xFF9F0712)
                            )
                        }
                    }
                }

                // Alert banner — only when pending count exceeds threshold
                if (uiState.showPendingAlert) {
                    item {
                        AlertBannerCard(pendingCount = uiState.pendingCount)
                    }
                }

                // Request list card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Todas las Solicitudes",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Color(0xFF1F2937)
                            )
                            Text(
                                text = "Haz click en una solicitud para ver detalles",
                                fontSize = 13.sp,
                                color = Color(0xFF9CA3AF),
                                modifier = Modifier.padding(top = 2.dp)
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            FilterChipRow(
                                options = listOf(
                                    FilterOption("Todos", uiState.totalCount, "ALL"),
                                    FilterOption("Pendientes", uiState.pendingCount, "PENDING"),
                                    FilterOption("Aprobadas", uiState.approvedCount, "APPROVED"),
                                    FilterOption("Rechazadas", uiState.rejectedCount, "REJECTED")
                                ),
                                selectedKey = uiState.activeFilter.name,
                                onFilterSelected = { key ->
                                    viewModel.onFilterChange(RequestFilter.valueOf(key))
                                }
                            )
                        }
                    }
                }

                // Filtered request cards
                items(
                    items = uiState.filteredRequests,
                    key = { it.id }
                ) { request ->
                    RequestCard(
                        item = request,
                        onClick = { viewModel.onRequestClick(request) }
                    )
                }

                // Bottom spacing
                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun DepartmentHeadDashboardScreenPreview() {
    DepartmentHeadDashboardScreen()
}
