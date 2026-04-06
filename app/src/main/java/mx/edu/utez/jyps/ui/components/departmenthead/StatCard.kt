package mx.edu.utez.jyps.ui.components.departmenthead

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
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

/**
 * Individual statistics card for the department head dashboard.
 * Shows a label, a large counter value, and a themed icon on the right.
 *
 * @param label Descriptive text displayed above the count (e.g. "Total Solicitudes").
 * @param count Numeric value to display prominently.
 * @param icon Material icon rendered inside a tinted circle.
 * @param iconBgColor Background tint for the icon container.
 * @param iconTint Foreground tint for the icon itself.
 */
@Composable
fun StatCard(
    label: String,
    count: Int,
    icon: ImageVector,
    iconBgColor: Color = Color(0xFFE8F0FE),
    iconTint: Color = Color(0xFF0F2C59)
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = label,
                    fontSize = 14.sp,
                    color = Color(0xFF6B7280)
                )
                Text(
                    text = count.toString(),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1F2937)
                )
            }

            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = iconTint,
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = iconBgColor,
                        shape = RoundedCornerShape(10.dp)
                    )
                    .padding(8.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun StatCardPreview() {
    StatCard(
        label = "Total Solicitudes",
        count = 38,
        icon = Icons.Default.Assessment
    )
}
