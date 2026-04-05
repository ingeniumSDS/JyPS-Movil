package mx.edu.utez.jyps.ui.screens.employee

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import mx.edu.utez.jyps.ui.components.cards.HistoryCard
import mx.edu.utez.jyps.ui.components.cards.HistoryItem
import mx.edu.utez.jyps.ui.components.navigation.AppBottomNavigationBar
import mx.edu.utez.jyps.ui.components.header.EmployeeHeader
import mx.edu.utez.jyps.ui.components.navigation.FilterTab
import mx.edu.utez.jyps.ui.components.status.HistoryStatus
import mx.edu.utez.jyps.ui.components.common.AppToast
import mx.edu.utez.jyps.ui.components.common.ToastType

enum class HistoryFilter { PASES, JUSTIFICANTES }

@Composable
fun EmployeeHistoryScreen(
    viewModel: mx.edu.utez.jyps.viewmodel.EmployeeHistoryViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onLogoutClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedFilter by remember { mutableStateOf(HistoryFilter.PASES) }

    // Mount dynamic dialogs based on ViewModel Reactivity
    if (uiState.requestToDelete != null) {
        mx.edu.utez.jyps.ui.components.dialogs.ConfirmDeleteDialog(
            onConfirm = { viewModel.confirmDelete() },
            onCancel = { viewModel.dismissDelete() }
        )
    }

    uiState.requestToEditPass?.let { item ->
        mx.edu.utez.jyps.ui.components.dialogs.EditPassDialog(
            item = item,
            onDismissRequest = { viewModel.dismissEditPass() },
            onSave = { newDetails, newTime ->
                viewModel.saveEditPass(item.id, newDetails, newTime)
            }
        )
    }

    uiState.requestToEditJustification?.let { item ->
        mx.edu.utez.jyps.ui.components.dialogs.EditJustificationDialog(
            item = item,
            onDismissRequest = { viewModel.dismissEditJustification() },
            onSave = { newDetails ->
                viewModel.saveEditJustification(item.id, newDetails)
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = { EmployeeHeader(userName = "Juan", onLogoutClick = onLogoutClick) },
            bottomBar = { AppBottomNavigationBar(selectedRoute = "historial", onHomeClick = onHomeClick, onProfileClick = onProfileClick) },
            containerColor = Color(0xFFF8F9FA)
        ) { padding ->
            Column(
                modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Historial", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Text("Tus solicitudes recientes", fontSize = 16.sp, color = Color(0xFF4A5565))
                }

                // Tabs/Filter
                Row(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(Color(0xFFF3F4F6)).padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterTab(
                        text = "Pases de Salida",
                        icon = Icons.Default.MeetingRoom,
                        isSelected = selectedFilter == HistoryFilter.PASES,
                        onClick = { selectedFilter = HistoryFilter.PASES },
                        modifier = Modifier.weight(1f)
                    )
                    FilterTab(
                        text = "Justificantes",
                        icon = Icons.Default.Description,
                        isSelected = selectedFilter == HistoryFilter.JUSTIFICANTES,
                        onClick = { selectedFilter = HistoryFilter.JUSTIFICANTES },
                        modifier = Modifier.weight(1f)
                    )
                }

                // List
                Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    val currentItems = if (selectedFilter == HistoryFilter.PASES) uiState.pases else uiState.justifications
                    currentItems.forEach { item ->
                        HistoryCard(
                            item = item,
                            onEdit = { 
                                if (item.type.contains("Pase")) viewModel.promptEditPass(item)
                                else viewModel.promptEditJustification(item)
                            },
                            onDelete = { viewModel.promptDelete(item.id) }
                        ) 
                    }
                    Spacer(Modifier.height(24.dp))
                }
            }
        }

        AppToast(
            message = uiState.opMessage,
            isVisible = uiState.isSuccessOp,
            onDismiss = { viewModel.clearOpMessage() },
            type = ToastType.SUCCESS,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Preview(showSystemUi = true)
@Composable
fun EmployeeHistoryPreview() {
    EmployeeHistoryScreen()
}
