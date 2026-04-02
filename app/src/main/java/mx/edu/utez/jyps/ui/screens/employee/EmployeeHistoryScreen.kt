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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mx.edu.utez.jyps.ui.components.cards.HistoryCard
import mx.edu.utez.jyps.ui.components.cards.HistoryItem
import mx.edu.utez.jyps.ui.components.navigation.AppBottomNavigationBar
import mx.edu.utez.jyps.ui.components.header.EmployeeHeader
import mx.edu.utez.jyps.ui.components.navigation.FilterTab
import mx.edu.utez.jyps.ui.components.status.HistoryStatus

enum class HistoryFilter { PASES, JUSTIFICANTES }

@Composable
fun EmployeeHistoryScreen(
    onLogoutClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    var selectedFilter by remember { mutableStateOf(HistoryFilter.PASES) }
    
    val pasesItems = listOf(
        HistoryItem("4", "Pase de Salida", HistoryStatus.APROBADO, "Salida a reunión externa", "27/3/2026", "11:00", "PASE004"),
        HistoryItem("5", "Pase de Salida", HistoryStatus.PENDIENTE, "Cita con dentista - Limpieza dental programada", "28/2/2026", "10:00", "PASE005", fileName = "comprobante_cita_dental.pdf"),
        HistoryItem("1", "Pase de Salida", HistoryStatus.USADO, "Trámite bancario - Gestión de crédito hipotecario", "25/2/2026", "14:30", "PASE001", internalInfo = "Este pase ya fue utilizado y no se puede usar más."),
        HistoryItem("2", "Pase de Salida", HistoryStatus.RECHAZADO, "Urgencia personal", "22/2/2026", "15:00", "PASE002", rejectionReason = "Motivo insuficiente. Se requiere documentación y más detalle sobre la urgencia."),
        HistoryItem("3", "Pase de Salida", HistoryStatus.CADUCADO, "Salida a comer", "20/2/2026", "13:00", "PASE003", rejectionReason = "Pase no utilizado durante la jornada.")
    )

    val justificantesItems = listOf(
        HistoryItem("1", "Justificante", HistoryStatus.APROBADO, "Consulta médica general - Revisión periódica", "24/2/2026", "10:00", "JUST001", fileName = "receta_medica.pdf"),
        HistoryItem("2", "Justificante", HistoryStatus.PENDIENTE, "Cita con dentista - Limpieza dental programada", "28/2/2026", "10:00", "JUST002", fileName = "comprobante_cita_dental.pdf"),
        HistoryItem("3", "Justificante", HistoryStatus.RECHAZADO, "Asunto personal sin documentación", "23/2/2026", "09:15", "JUST003", rejectionReason = "No se proporcionó evidencia médica o motivo válido.")
    )

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
                Text("Tus solicitudes recientes (últimas 5)", fontSize = 16.sp, color = Color(0xFF4A5565))
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
                val currentItems = if (selectedFilter == HistoryFilter.PASES) pasesItems else justificantesItems
                currentItems.forEach { HistoryCard(it) }
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun EmployeeHistoryPreview() {
    EmployeeHistoryScreen()
}

