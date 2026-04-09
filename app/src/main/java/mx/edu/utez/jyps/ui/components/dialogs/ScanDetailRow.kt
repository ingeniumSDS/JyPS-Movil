package mx.edu.utez.jyps.ui.components.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp

/**
 * Single row inside the pass info grid.
 *
 * @param label Descriptive label on the left.
 * @param value Formatted value on the right.
 * @param valueColor Optional override color for the value text.
 * @param isMono Renders [value] in a monospace font for code-like fields.
 */
@Composable
fun ScanDetailRow(
    label: String,
    value: String,
    valueColor: Color = Color(0xFF0F2C59),
    isMono: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Text(text = label, fontSize = 13.sp, color = Color(0xFF64748B), modifier = Modifier.weight(0.45f))
        Text(
            text = value,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = valueColor,
            fontFamily = if (isMono) FontFamily.Monospace else FontFamily.Default,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(0.55f)
        )
    }
}


@Preview(showBackground = true)
@Composable
fun ScanDetailRowPreview() {
    ScanDetailRow(
        label = "Hora de salida",
        value = "09:30 a.m."
    )
}
