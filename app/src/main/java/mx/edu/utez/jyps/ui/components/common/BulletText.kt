package mx.edu.utez.jyps.ui.components.common

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Reusable bullet list item for displaying formatted lists across the application.
 *
 * @param text The string content of the bullet point.
 * @param fontSize Font size for the text element. Defaults to 12.sp.
 */
@Composable
fun BulletText(
    text: String,
    fontSize: TextUnit = 12.sp
) {
    Row(verticalAlignment = Alignment.Top, modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "• ",
            fontSize = fontSize,
            color = Color(0xFF364153),
            modifier = Modifier.padding(end = 4.dp)
        )
        Text(
            text = text,
            fontSize = fontSize,
            color = Color(0xFF364153),
            lineHeight = 16.sp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun BulletTextPreview() {
    BulletText("Ejemplo de texto de viñeta")
}
