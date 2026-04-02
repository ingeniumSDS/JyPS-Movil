package mx.edu.utez.jyps.ui.components.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import mx.edu.utez.jyps.ui.theme.JyPSTheme

/**
 * Reusable tab button for filtering items.
 *
 * @param text The text label for the tab.
 * @param icon The vector icon to display next to the text.
 * @param isSelected Whether the tab is currently active.
 * @param onClick The callback triggered when the tab is clicked.
 * @param modifier The modifier to be applied to the tab.
 */
@Composable
fun FilterTab(
    text: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        shape = RoundedCornerShape(6.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) Color.White else Color.Transparent,
            contentColor = if (isSelected) MaterialTheme.colorScheme.primary else Color(0xFF4A5565)
        ),
        elevation = if (isSelected) ButtonDefaults.buttonElevation(2.dp) else null,
        contentPadding = PaddingValues(horizontal = 8.dp)
    ) {
        Icon(icon, null, Modifier.size(18.dp))
        Spacer(Modifier.width(8.dp))
        Text(text, fontSize = 14.sp, fontWeight = FontWeight.Medium)
    }
}

@Preview(showBackground = true)
@Composable
fun FilterTabPreview() {
    JyPSTheme {
        FilterTab(text = "Filtro", icon = Icons.Default.List, isSelected = true, onClick = {})
    }
}
