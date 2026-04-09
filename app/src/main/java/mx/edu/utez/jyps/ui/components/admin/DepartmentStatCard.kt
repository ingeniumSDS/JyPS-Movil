package mx.edu.utez.jyps.ui.components.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mx.edu.utez.jyps.ui.theme.JyPSTheme

/**
 * A statistics card for the department management dashboard.
 *
 * @param title Label of the statistic (e.g., "Activos").
 * @param count Numerical value to display.
 * @param icon The vector icon representing this stat.
 * @param iconBackground Color for the icon's container.
 * @param textColor Color for the count value.
 */
@Composable
fun DepartmentStatCard(
    title: String,
    count: Int,
    icon: ImageVector,
    iconBackground: Color,
    textColor: Color = Color(0xFF0F2C59),
    modifier: Modifier = Modifier,
    isCentered: Boolean = false
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(if (isCentered) 24.dp else 16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = if (isCentered) Arrangement.Center else Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(iconBackground, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = textColor.copy(alpha = 0.8f),
                    modifier = Modifier.size(20.dp)
                )
            }

            if (isCentered) Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = title,
                    fontSize = 12.sp,
                    color = Color(0xFF4A5565),
                    fontWeight = FontWeight.Normal
                )
                Text(
                    text = count.toString(),
                    fontSize = 24.sp,
                    color = textColor,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DepartmentStatCardPreview() {
    JyPSTheme {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            DepartmentStatCard(
                title = "Total Departamentos",
                count = 8,
                icon = Icons.Default.Business,
                iconBackground = Color(0xFF0F2C59).copy(alpha = 0.1f)
            )
            DepartmentStatCard(
                title = "Activos",
                count = 7,
                icon = Icons.Default.CheckCircle,
                iconBackground = Color(0xFFDCFCE7),
                textColor = Color(0xFF00A63E)
            )
        }
    }
}
