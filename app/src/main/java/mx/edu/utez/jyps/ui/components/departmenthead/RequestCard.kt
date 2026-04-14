package mx.edu.utez.jyps.ui.components.departmenthead

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mx.edu.utez.jyps.data.model.RequestItem
import mx.edu.utez.jyps.data.model.RequestStatus
import mx.edu.utez.jyps.data.model.RequestType

/**
 * Individual request card in the Department Head's "Todas las Solicitudes" list.
 * Shows employee avatar (initial), name, request type + reason, date/time, and a coloured badge.
 *
 * @param item The request data to display.
 * @param onClick Callback when the card is tapped.
 */
@Composable
fun RequestCard(
    item: RequestItem,
    onClick: () -> Unit
) {
    val typeLabel = when (item.requestType) {
        RequestType.PASS -> "Pase de Salida"
        RequestType.JUSTIFICATION -> "Justificante"
    }

    val typeIcon = when (item.requestType) {
        RequestType.PASS -> Icons.Default.MeetingRoom
        RequestType.JUSTIFICATION -> Icons.Default.Description
    }

    val iconTint = when (item.requestType) {
        RequestType.PASS -> Color(0xFF0F2C59)
        RequestType.JUSTIFICATION -> Color(0xFF28A745)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Top row: icon + name + type+reason
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE8F0FE)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = typeIcon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Column(modifier = Modifier.padding(start = 12.dp)) {
                    Text(
                        text = item.employeeName,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                        color = Color(0xFF1F2937)
                    )
                    Text(
                        text = "$typeLabel • ${item.reason}",
                        fontSize = 13.sp,
                        color = Color(0xFF6B7280),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                color = Color(0xFFF3F4F6)
            )

            // Bottom row: date/time + badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = item.date, fontSize = 13.sp, color = Color(0xFF6B7280))
                    Text(text = item.time, fontSize = 13.sp, color = Color(0xFF6B7280))
                }

                RequestStatusBadge(status = item.status)
            }
        }
    }
}



@Preview(showBackground = true)
@Composable
fun RequestCardPreview() {
    RequestCard(
        item = RequestItem(
            id = "1",
            numericId = 1L,
            employeeName = "Juan Pérez García",
            employeeEmail = "juan.perez@utez.edu.mx",
            requestType = RequestType.PASS,
            reason = "Cita con dentista - Limpieza dental programada",
            date = "27/2/2026",
            time = "08:30 a.m.",
            exitTime = "10:00",
            status = RequestStatus.PENDING
        ),
        onClick = {}
    )
}
