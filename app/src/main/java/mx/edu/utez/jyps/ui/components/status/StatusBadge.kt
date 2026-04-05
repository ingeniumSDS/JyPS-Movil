package mx.edu.utez.jyps.ui.components.status

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.TimerOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mx.edu.utez.jyps.ui.theme.*
import androidx.compose.ui.tooling.preview.Preview

enum class HistoryStatus { USADO, PENDIENTE, RECHAZADO, CADUCADO, APROBADO }

/**
 * Reusable badge to display status with colors and icons.
 *
 * @param status The status enum value to be displayed.
 */
@Composable
fun StatusBadge(status: HistoryStatus) {
    val (bgColor, textColor, icon) = when (status) {
        HistoryStatus.USADO -> Triple(UsedGrayBg, UsedGray, Icons.Default.CheckCircle)
        HistoryStatus.PENDIENTE -> Triple(PendingYellowBg, PendingYellow, Icons.Default.Schedule)
        HistoryStatus.RECHAZADO -> Triple(ErrorRedBg, ErrorRed, Icons.Default.Cancel)
        HistoryStatus.CADUCADO -> Triple(UsedGrayBg, SecondaryColor, Icons.Default.TimerOff)
        HistoryStatus.APROBADO -> Triple(SuccessGreenBg, SuccessGreen, Icons.Default.CheckCircle)
    }
    
    Surface(
        color = bgColor,
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(12.dp),
                tint = textColor
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = status.name.lowercase().replaceFirstChar { it.uppercase() },
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun StatusBadgePreview() {
    JyPSTheme {
        StatusBadge(status = HistoryStatus.PENDIENTE)
    }
}
