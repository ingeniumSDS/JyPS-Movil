package mx.edu.utez.jyps.ui.components.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.Icons
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Simple row structure standardizing icon and values.
 *
 * @param icon The prefix indicator vector.
 * @param label Top descriptive string text.
 * @param value Principal visual text definition array.
 */
@Composable
fun DetailRow(icon: ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Icon(icon, contentDescription = null, tint = Color(0xFF6A7282), modifier = Modifier.size(18.dp))
        Column {
            Text(label, fontSize = 12.sp, color = Color.Gray)
            Text(value, fontSize = 14.sp, color = Color(0xFF101828), fontWeight = FontWeight.Medium)
        }
    }
}


@Preview(showBackground = true)
@Composable
fun DetailRowPreview() {
    DetailRow(
        icon = Icons.Default.Email,
        label = "Correo",
        value = "usuario@utez.edu.mx"
    )
}
