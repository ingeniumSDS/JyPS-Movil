package mx.edu.utez.jyps.ui.components.admin

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * A row component used to display a contact metric with an icon.
 *
 * @param icon Material image vector to display in front of the text.
 * @param text The contact value (phone, email).
 * @param dimmed Whether to apply a visually dimmed color scheme.
 */
@Composable
fun ContactRow(icon: ImageVector, text: String, dimmed: Boolean = false) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = Color(0xFF99A1AF), modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text, color = if (dimmed) Color(0xFF6A7282) else Color(0xFF4A5565), fontSize = 14.sp)
    }
}

@Preview(showBackground = true)
@Composable
fun ContactRowPreview() {
    ContactRow(icon = Icons.Default.Email, text = "test@example.com")
}
