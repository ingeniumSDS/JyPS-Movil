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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mx.edu.utez.jyps.data.model.EstadosIncidencia
import mx.edu.utez.jyps.ui.theme.*
import androidx.compose.ui.tooling.preview.Preview

/**
 * Reusable badge to display status with colors and icons.
 *
 * @param status The status enum value to be displayed.
 */
@Composable
fun StatusBadge(status: EstadosIncidencia) {
    val (bgColor, textColor, icon) = when (status) {
        EstadosIncidencia.USADO -> Triple(UsedGrayBg, UsedGray, Icons.Default.CheckCircle)
        EstadosIncidencia.PENDIENTE -> Triple(PendingYellowBg, PendingYellow, Icons.Default.Schedule)
        EstadosIncidencia.RECHAZADO -> Triple(ErrorRedBg, ErrorRed, Icons.Default.Cancel)
        EstadosIncidencia.CADUCADO -> Triple(UsedGrayBg, SecondaryColor, Icons.Default.TimerOff)
        EstadosIncidencia.APROBADO -> Triple(SuccessGreenBg, SuccessGreen, Icons.Default.CheckCircle)
        EstadosIncidencia.A_TIEMPO -> Triple(SuccessGreenBg, SuccessGreen, Icons.Default.CheckCircle)
        EstadosIncidencia.RETARDO -> Triple(PendingYellowBg, PendingYellow, Icons.Default.Schedule)
        EstadosIncidencia.FUERA -> Triple(SuccessGreenBg, SuccessGreen, Icons.Default.CheckCircle)
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
        StatusBadge(status = EstadosIncidencia.PENDIENTE)
    }
}
