package mx.edu.utez.jyps.ui.components.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mx.edu.utez.jyps.data.model.HistoryItem
import mx.edu.utez.jyps.data.model.EstadosIncidencia

/**
 * Dialog displaying detailed information about an exit pass request.
 *
 * @param item The exit pass history record to show.
 * @param onDismissRequest Callback to close the dialog.
 */
@Composable
fun PassDetailDialog(
    item: HistoryItem,
    onDismissRequest: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Cerrar")
            }
        },
        title = {
            Text(
                text = "Detalles del Pase de Salida",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                StatusBadge(status = item.status)

                DetailRow(
                    icon = Icons.Default.QrCode,
                    label = "Código / Folio",
                    value = item.code
                )

                DetailRow(
                    icon = Icons.Default.CalendarMonth,
                    label = "Fecha de Salida",
                    value = item.date
                )

                DetailRow(
                    icon = Icons.Default.AccessTime,
                    label = "Hora Programada",
                    value = item.time
                )

                DetailRow(
                    icon = Icons.Default.Description,
                    label = "Motivo de la Salida",
                    value = item.description
                )

                item.rejectionReason?.let {
                    DetailRow(
                        icon = Icons.Default.Info,
                        label = "Comentarios de Rechazo",
                        value = it,
                        valueColor = Color.Red
                    )
                }
            }
        },
        shape = RoundedCornerShape(16.dp)
    )
}

/**
 * Internal UI component to render a labeled data row with an icon.
 */
@Composable
private fun DetailRow(
    icon: ImageVector,
    label: String,
    value: String,
    valueColor: Color = Color.Unspecified
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = label,
                fontSize = 12.sp,
                color = Color.Gray,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = value,
                fontSize = 15.sp,
                fontWeight = FontWeight.Normal,
                color = valueColor
            )
        }
    }
}

/**
 * Status indicator badge with contextual colors.
 */
@Composable
private fun StatusBadge(status: EstadosIncidencia) {
    val (backgroundColor, textColor) = when (status) {
        EstadosIncidencia.APROBADO -> Color(0xFFE8F5E9) to Color(0xFF2E7D32)
        EstadosIncidencia.USADO -> Color(0xFFE3F2FD) to Color(0xFF1976D2)
        EstadosIncidencia.RECHAZADO -> Color(0xFFFFEBEE) to Color(0xFFC62828)
        else -> Color(0xFFFFF3E0) to Color(0xFFEF6C00)
    }

    Surface(
        color = backgroundColor,
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = status.name,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PassDetailDialogPreview() {
    PassDetailDialog(
        item = HistoryItem(
            id = "2",
            type = "Pase de Salida",
            status = EstadosIncidencia.APROBADO,
            description = "Trámite administrativo en rectoría.",
            date = "15/04/2026",
            time = "10:30 AM",
            code = "PASE-442"
        ),
        onDismissRequest = {}
    )
}
