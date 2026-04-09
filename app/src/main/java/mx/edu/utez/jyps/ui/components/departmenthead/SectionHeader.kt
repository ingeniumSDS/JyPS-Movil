package mx.edu.utez.jyps.ui.components.departmenthead

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.Icons
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Reusable section header with an icon and a bold title.
 *
 * @param icon The prefix vector icon for the header sequence.
 * @param title Bold text defining the local area property context.
 */
@Composable
fun SectionHeader(
    icon: ImageVector,
    title: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(18.dp)
        )
        Text(
            text = title,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = Color(0xFF1F2937),
            modifier = Modifier.padding(start = 6.dp)
        )
    }
}


@Preview(showBackground = true)
@Composable
fun SectionHeaderPreview() {
    SectionHeader(
        icon = Icons.Default.Person,
        title = "Información del Empleado"
    )
}
