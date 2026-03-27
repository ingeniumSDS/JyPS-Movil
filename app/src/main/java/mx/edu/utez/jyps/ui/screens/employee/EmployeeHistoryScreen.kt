package mx.edu.utez.jyps.ui.screens.employee

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class HistoryFilter { PASES, JUSTIFICANTES }
enum class HistoryStatus { USADO, PENDIENTE, RECHAZADO, CADUCADO, APROBADO }

data class HistoryItem(
    val id: String,
    val type: String,
    val status: HistoryStatus,
    val description: String,
    val date: String,
    val time: String,
    val code: String,
    val fileName: String? = null,
    val rejectionReason: String? = null,
    val internalInfo: String? = null
)

@Composable
fun EmployeeHistoryScreen(
    onLogoutClick: () -> Unit = {},
    onHomeClick: () -> Unit = {}
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
        bottomBar = { AppBottomNavigation(selectedRoute = "historial", onHomeClick = onHomeClick) },
        containerColor = Color(0xFFF8F9FA)
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Historial", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F2C59))
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

@Composable
fun FilterTab(text: String, icon: ImageVector, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        shape = RoundedCornerShape(6.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) Color.White else Color.Transparent,
            contentColor = if (isSelected) Color(0xFF0F2C59) else Color(0xFF4A5565)
        ),
        elevation = if (isSelected) ButtonDefaults.buttonElevation(2.dp) else null,
        contentPadding = PaddingValues(horizontal = 8.dp)
    ) {
        Icon(icon, null, Modifier.size(18.dp))
        Spacer(Modifier.width(8.dp))
        Text(text, fontSize = 14.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun HistoryCard(item: HistoryItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(Modifier.size(48.dp).clip(CircleShape).background(Color(0xFFDBEAFE)), Alignment.Center) {
                Icon(if (item.type.contains("Pase")) Icons.Default.MeetingRoom else Icons.Default.Description, null, tint = Color(0xFF0F2C59))
            }
            
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(item.type, fontWeight = FontWeight.SemiBold, color = Color(0xFF0F2C59))
                    StatusBadge(item.status)
                }
                
                Text(item.description, fontSize = 14.sp, color = Color(0xFF4A5565))
                
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("📅 ${item.date}", fontSize = 12.sp, color = Color(0xFF6A7282))
                    Text("🕐 ${item.time}", fontSize = 12.sp, color = Color(0xFF6A7282))
                    
                    // Solo mostrar la llave si NO está pendiente ni rechazado
                    if (item.status != HistoryStatus.PENDIENTE && item.status != HistoryStatus.RECHAZADO && item.status != HistoryStatus.CADUCADO) {
                        Text("🔑 ${item.code}", fontSize = 12.sp, color = Color(0xFF6A7282))
                    }
                }

                item.fileName?.let { Text("📎 $it", fontSize = 12.sp, color = Color(0xFF155DFC)) }

                item.internalInfo?.let { 
                    Box(Modifier.fillMaxWidth().background(Color(0xFFF3F4F6), RoundedCornerShape(8.dp)).border(0.5.dp, Color(0xFFD1D5DC), RoundedCornerShape(8.dp)).padding(8.dp)) {
                        Text("ℹ️ $it", fontSize = 12.sp, color = Color(0xFF364153))
                    }
                }

                item.rejectionReason?.let {
                    val label = if (item.status == HistoryStatus.RECHAZADO) "Motivo de rechazo: " else "Motivo: "
                    val bgColor = if (item.status == HistoryStatus.CADUCADO) Color(0xFFF3F4F6) else Color(0xFFFEF2F2)
                    val textColor = if (item.status == HistoryStatus.CADUCADO) Color(0xFF4A5565) else Color(0xFF9F0712)
                    Box(Modifier.fillMaxWidth().background(bgColor, RoundedCornerShape(8.dp)).padding(8.dp)) {
                        Text("$label$it", fontSize = 12.sp, color = textColor, fontWeight = FontWeight.Bold)
                    }
                }

                // Las acciones solo aparecen si está PENDIENTE
                if (item.status == HistoryStatus.PENDIENTE) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = {}, Modifier.weight(1f), shape = RoundedCornerShape(8.dp)) { Text("Editar", fontSize = 12.sp) }
                        OutlinedButton(onClick = {}, Modifier.weight(1f), shape = RoundedCornerShape(8.dp), colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red)) { Text("Eliminar", fontSize = 12.sp) }
                    }
                }
            }
        }
    }
}

@Composable
fun StatusBadge(status: HistoryStatus) {
    val (bgColor, textColor, icon) = when (status) {
        HistoryStatus.USADO -> Triple(Color(0xFFF3F4F6), Color(0xFF1E2939), Icons.Default.CheckCircle)
        HistoryStatus.PENDIENTE -> Triple(Color(0xFFFEF9C2), Color(0xFF894B00), Icons.Default.Schedule)
        HistoryStatus.RECHAZADO -> Triple(Color(0xFFFFE2E2), Color(0xFF9F0712), Icons.Default.Cancel)
        HistoryStatus.CADUCADO -> Triple(Color(0xFFE5E7EB), Color(0xFF4A5565), Icons.Default.TimerOff)
        HistoryStatus.APROBADO -> Triple(Color(0xFFDCFCE7), Color(0xFF016630), Icons.Default.CheckCircle)
    }
    
    Surface(color = bgColor, shape = RoundedCornerShape(16.dp)) {
        Row(Modifier.padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, Modifier.size(12.dp), textColor)
            Spacer(Modifier.width(4.dp))
            Text(status.name.lowercase().replaceFirstChar { it.uppercase() }, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = textColor)
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun EmployeeHistoryPreview() { EmployeeHistoryScreen() }
