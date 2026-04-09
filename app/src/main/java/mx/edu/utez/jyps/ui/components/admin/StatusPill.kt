package mx.edu.utez.jyps.ui.components.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Standard pill for displaying dynamic warning statuses.
 *
 * @param status Status description text.
 * @param textColor Color tint for the text and icon.
 * @param bgColor Fill color value for the background.
 */
@Composable
fun StatusPill(status: String, textColor: Color, bgColor: Color) {
    Row(
        modifier = Modifier.background(bgColor, RoundedCornerShape(12.dp)).padding(horizontal = 12.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.Warning, contentDescription = null, tint = textColor, modifier = Modifier.size(12.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = status, color = textColor, fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}

@Preview
@Composable
fun StatusPillPreview() {
    StatusPill(status = "Bloqueada", textColor = Color(0xFFDC2626), bgColor = Color(0xFFFEE2E2))
}
