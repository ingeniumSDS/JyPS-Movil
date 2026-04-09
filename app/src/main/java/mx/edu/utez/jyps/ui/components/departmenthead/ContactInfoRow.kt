package mx.edu.utez.jyps.ui.components.departmenthead

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.Icons
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Renders a standardized combination of vector and label text.
 *
 * @param icon Preceding vector graphics.
 * @param text Label string shown.
 */
@Composable
fun ContactInfoRow(
    icon: ImageVector,
    text: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFF6A7282),
            modifier = Modifier.size(14.dp)
        )
        Text(
            text = text,
            fontSize = 12.sp,
            color = Color(0xFF6A7282)
        )
    }
}


@Preview(showBackground = true)
@Composable
fun ContactInfoRowPreview() {
    ContactInfoRow(
        icon = Icons.Default.Phone,
        text = "777-123-4567"
    )
}
