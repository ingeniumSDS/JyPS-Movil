package mx.edu.utez.jyps.ui.components.cards

import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mx.edu.utez.jyps.ui.components.status.HistoryStatus
import mx.edu.utez.jyps.ui.components.status.StatusBadge
import mx.edu.utez.jyps.ui.theme.*
import androidx.compose.ui.tooling.preview.Preview

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

/**
 * Reusable card for history items (Passes or Justifications).
 *
 * @param item The HistoryItem data object containing all details for this history entry.
 */
@Composable
fun HistoryCard(
    item: HistoryItem,
    onEdit: () -> Unit = {},
    onDelete: () -> Unit = {},
    onShowQr: () -> Unit = {}
) {
    val isClickable = item.status == HistoryStatus.APROBADO && item.type.contains("Pase")
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (isClickable) Modifier.clickable { onShowQr() } else Modifier),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFDBEAFE)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (item.type.contains("Pase")) Icons.Default.MeetingRoom else Icons.Default.Description,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = item.type,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    StatusBadge(item.status)
                }
                
                Text(
                    text = item.description,
                    fontSize = 14.sp,
                    color = Color(0xFF4A5565)
                )
                
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("📅 ${item.date}", fontSize = 12.sp, color = Color(0xFF6A7282))
                    Text("🕐 ${item.time}", fontSize = 12.sp, color = Color(0xFF6A7282))
                    
                    if (item.status != HistoryStatus.PENDIENTE && 
                        item.status != HistoryStatus.RECHAZADO && 
                        item.status != HistoryStatus.CADUCADO) {
                        Text("🔑 ${item.code}", fontSize = 12.sp, color = Color(0xFF6A7282))
                    }
                }

                item.fileName?.let {
                    Text("📎 $it", fontSize = 12.sp, color = Color(0xFF155DFC))
                }

                item.internalInfo?.let { 
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF3F4F6), RoundedCornerShape(8.dp))
                            .border(0.5.dp, Color(0xFFD1D5DC), RoundedCornerShape(8.dp))
                            .padding(8.dp)
                    ) {
                        Text("ℹ️ $it", fontSize = 12.sp, color = Color(0xFF364153))
                    }
                }

                item.rejectionReason?.let {
                    val label = if (item.status == HistoryStatus.RECHAZADO) "Motivo de rechazo: " else "Motivo: "
                    val bgColor = if (item.status == HistoryStatus.CADUCADO) UsedGrayBg else ErrorRedBg
                    val textColor = if (item.status == HistoryStatus.CADUCADO) SecondaryColor else ErrorRed
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(bgColor, RoundedCornerShape(8.dp))
                            .padding(8.dp)
                    ) {
                        Text(
                            text = "$label$it",
                            fontSize = 12.sp,
                            color = textColor,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                if (item.status == HistoryStatus.APROBADO && item.type.contains("Pase")) {
                    Button(
                        onClick = onShowQr,
                        modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF0F4FF))
                    ) {
                        Text("💡 Toca aquí para ver tu código QR", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }

                if (item.status == HistoryStatus.PENDIENTE) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            onClick = onEdit,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Editar", fontSize = 12.sp)
                        }
                        OutlinedButton(
                            onClick = onDelete,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red)
                        ) {
                            Text("Eliminar", fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HistoryCardPreview() {
    JyPSTheme {
        HistoryCard(
            item = HistoryItem(
                id = "1",
                type = "Pase de Salida",
                status = HistoryStatus.APROBADO,
                description = "Reunión externa con cliente.",
                date = "22/10/2026",
                time = "10:30",
                code = "1234XYZ"
            )
        )
    }
}
