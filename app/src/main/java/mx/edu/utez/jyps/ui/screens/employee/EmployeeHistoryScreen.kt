package mx.edu.utez.jyps.ui.screens.employee

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import mx.edu.utez.jyps.ui.components.cards.HistoryCard
import mx.edu.utez.jyps.ui.components.dialogs.ApprovedPassQrDialog
import mx.edu.utez.jyps.ui.components.dialogs.ConfirmDeleteDialog
import mx.edu.utez.jyps.ui.components.dialogs.EditJustificationDialog
import mx.edu.utez.jyps.ui.components.dialogs.EditPassDialog
import mx.edu.utez.jyps.ui.components.header.EmployeeHeader
import mx.edu.utez.jyps.ui.components.navigation.AppBottomNavigationBar
import mx.edu.utez.jyps.ui.components.navigation.FilterTab
import mx.edu.utez.jyps.data.model.HistoryFilter
import mx.edu.utez.jyps.ui.components.common.EmployeeModeBanner
import mx.edu.utez.jyps.utils.ImageUtils
import mx.edu.utez.jyps.viewmodel.EmployeeHistoryViewModel

/**
 * Screen displaying the employee's history of exit passes and justifications.
 */
@Composable
fun EmployeeHistoryScreen(
    onLogoutClick: () -> Unit,
    onHomeClick: () -> Unit,
    onProfileClick: () -> Unit,
    viewModel: EmployeeHistoryViewModel,
    showEmployeeModeBanner: Boolean = false,
    onReturnToRoleDashboard: () -> Unit = {},
    userName: String = "Empleado"
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedFilter by remember { mutableStateOf(HistoryFilter.PASES) }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // Display SnackBar messages from the ViewModel
    LaunchedEffect(uiState.isSuccessOp) {
        if (uiState.isSuccessOp) {
            uiState.opMessage?.let { msg ->
                snackbarHostState.showSnackbar(msg)
                viewModel.clearOpMessage()
            }
        }
    }

    // Modal Overlays
    if (uiState.requestToDelete != null) {
        ConfirmDeleteDialog(
            onCancel = { viewModel.dismissDelete() },
            onConfirm = { viewModel.confirmDelete() }
        )
    }

    uiState.requestToEditPass?.let { item ->
        EditPassDialog(
            item = item,
            onDismissRequest = { viewModel.dismissEditPass() },
            onSave = { newDetails, newTime ->
                viewModel.saveEditPass(item.id, newDetails, newTime)
            }
        )
    }

    uiState.requestToEditJustification?.let { item ->
        EditJustificationDialog(
            item = item,
            onDismissRequest = { viewModel.dismissEditJustification() },
            onSave = { newDetails ->
                viewModel.saveEditJustification(item.id, newDetails)
            }
        )
    }

    uiState.requestToShowQr?.let { item ->
        ApprovedPassQrDialog(
            item = item,
            onDismissRequest = { viewModel.dismissShowQr() },
            onDownloadMock = { bitmap ->
                coroutineScope.launch {
                    val success = ImageUtils.saveBitmapToGallery(context, bitmap, item.code)
                    viewModel.dispatchDownloadQrResult(success)
                }
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = { EmployeeHeader(userName = userName, onLogoutClick = onLogoutClick) },
            bottomBar = { 
                AppBottomNavigationBar(
                    selectedRoute = "historial",
                    onHomeClick = onHomeClick,
                    onProfileClick = onProfileClick
                ) 
            },
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                // Employee Mode banner — shown when accessed from DeptHead or Admin
                if (showEmployeeModeBanner) {
                    EmployeeModeBanner(onBackClick = onReturnToRoleDashboard)
                }

                // Filter Selection Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF1F5F9))
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterTab(
                        text = "Pases",
                        icon = Icons.Default.MeetingRoom,
                        isSelected = selectedFilter == HistoryFilter.PASES,
                        onClick = { selectedFilter = HistoryFilter.PASES },
                        modifier = Modifier.weight(1f)
                    )
                    FilterTab(
                        text = "Justificantes",
                        icon = Icons.Default.Description,
                        isSelected = selectedFilter == HistoryFilter.JUSTIFICACIONES,
                        onClick = { selectedFilter = HistoryFilter.JUSTIFICACIONES },
                        modifier = Modifier.weight(1f)
                    )
                }

                // History List
                val listItems = if (selectedFilter == HistoryFilter.PASES) uiState.pases else uiState.justifications
                
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(listItems, key = { it.id }) { item ->
                        HistoryCard(
                            item = item,
                            onEdit = { 
                                if (selectedFilter == HistoryFilter.PASES) viewModel.promptEditPass(item) 
                                else viewModel.promptEditJustification(item) 
                            },
                            onDelete = { viewModel.promptDelete(item.id) },
                            onShowQr = { viewModel.promptShowQr(item) }
                        )
                    }
                }
            }
        }
    }
}
