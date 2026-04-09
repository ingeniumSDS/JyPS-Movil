package mx.edu.utez.jyps.ui.components.admin

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Filter button component in the shape of a pill.
 *
 * @param text The label on the pill.
 * @param isSelected Whether this filter is currently active.
 * @param onClick Callback when the pill is pressed.
 */
@Composable
fun FilterPill(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val containerColor = if (isSelected) Color(0xFF0F2C59) else Color.White
    val contentColor = if (isSelected) Color.White else Color(0xFF0F2C59)
    val borderColor = Color(0xFF0F2C59)

    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        shape = RoundedCornerShape(8.dp),
        border = if (!isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, borderColor) else null,
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
        modifier = Modifier.height(48.dp)
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Preview
@Composable
fun FilterPillPreview() {
    FilterPill(text = "Activos", isSelected = true, onClick = {})
}
