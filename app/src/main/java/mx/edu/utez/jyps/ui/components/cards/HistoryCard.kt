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
import mx.edu.utez.jyps.data.model.EstadosIncidencia
import mx.edu.utez.jyps.ui.components.status.StatusBadge
import mx.edu.utez.jyps.ui.theme.*
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import mx.edu.utez.jyps.data.model.HistoryItem
import mx.edu.utez.jyps.utils.FileUtils

/**
 * Reusable card for history items (Passes or Justifications).
 *
 * @param item The HistoryItem data object containing all details for this history entry.
 * @param onEdit Lambda executed when the user chooses to modify a pending request.
 * @param onDelete Lambda executed when the user opts to discard a pending request.
 * @param onShowQr Lambda displaying the generated visual matrix upon confirmation click.
 * @param onClick Lambda executed when the entire card is tapped to show details.
 */
@Composable
fun HistoryCard(
    item: HistoryItem,
    onEdit: () -> Unit = {},
    onDelete: () -> Unit = {},
    onShowQr: () -> Unit = {},
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
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
                    
                    if (item.type != "Justificante") {
                        Text("🕐 ${item.time}", fontSize = 12.sp, color = Color(0xFF6A7282))
                    }
                    
                    if (item.status != EstadosIncidencia.PENDIENTE && 
                        item.status != EstadosIncidencia.RECHAZADO && 
                        item.status != EstadosIncidencia.CADUCADO) {
                        
                        val codeLabel = if (item.type == "Justificante") "JUST_${item.id}" else item.code
                        Text("🔑 $codeLabel", fontSize = 12.sp, color = Color(0xFF6A7282))
                    }
                }

                if (item.attachments.isNotEmpty()) {
                    val firstFile = item.attachments.first()
                    val fileName = FileUtils.formatFileName(firstFile.displayName)
                    val extraText = if (item.attachments.size > 1) " y ${item.attachments.size - 1} más" else ""
                    
                    Text(
                        text = "📎 $fileName$extraText",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium,
                        textDecoration = TextDecoration.Underline
                    )
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
                    val label = if (item.status == EstadosIncidencia.RECHAZADO) "Motivo de rechazo: " else "Motivo: "
                    val bgColor = if (item.status == EstadosIncidencia.CADUCADO) UsedGrayBg else ErrorRedBg
                    val textColor = if (item.status == EstadosIncidencia.CADUCADO) SecondaryColor else ErrorRed
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

                if (item.status == EstadosIncidencia.APROBADO && item.type.contains("Pase")) {
                    Button(
                        onClick = onShowQr,
                        modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF0F4FF))
                    ) {
                        Text("💡 Toca aquí para ver tu código QR", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }

                if (item.status == EstadosIncidencia.PENDIENTE) {
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
                status = EstadosIncidencia.APROBADO,
                description = "Reunión externa con cliente.",
                date = "22/10/2026",
                time = "10:30",
                code = "1234XYZ"
            )
        )
    }
}
