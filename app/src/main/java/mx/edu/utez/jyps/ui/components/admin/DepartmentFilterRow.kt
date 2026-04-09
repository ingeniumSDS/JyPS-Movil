package mx.edu.utez.jyps.ui.components.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import mx.edu.utez.jyps.ui.components.inputs.AppTextField
import mx.edu.utez.jyps.ui.theme.JyPSTheme

/**
 * A combined search and filter component for department management.
 *
 * @param searchQuery Current text in the search bar.
 * @param onSearchQueryChange Callback for text changes.
 * @param selectedFilter Currently active filter (Todos, Activos, Inactivos).
 * @param onFilterChange Callback for filter selection.
 */
@Composable
fun DepartmentFilterRow(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    selectedFilter: String,
    onFilterChange: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Search Bar
            AppTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                label = "",
                placeholder = "Buscar por nombre o descripción...",
                leadingIcon = Icons.Default.Search
            )

            // Filter Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterButton(
                    text = "Todos",
                    isSelected = selectedFilter == "Todos",
                    activeColor = Color(0xFF0F2C59),
                    onClick = { onFilterChange("Todos") }
                )
                FilterButton(
                    text = "Activos",
                    icon = Icons.Default.CheckCircle,
                    isSelected = selectedFilter == "Activos",
                    activeColor = Color(0xFF28A745),
                    onClick = { onFilterChange("Activos") }
                )
                FilterButton(
                    text = "Inactivos",
                    icon = Icons.Default.Cancel,
                    isSelected = selectedFilter == "Inactivos",
                    activeColor = Color(0xFFDC3545),
                    onClick = { onFilterChange("Inactivos") }
                )
            }
        }
    }
}

@Composable
private fun FilterButton(
    text: String,
    isSelected: Boolean,
    activeColor: Color,
    icon: ImageVector? = null,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) activeColor else Color.Transparent
    val contentColor = if (isSelected) Color.White else Color(0xFF0F2C59)
    val borderColor = if (isSelected) activeColor else Color(0xFF0F2C59)

    Row(
        modifier = Modifier
            .background(backgroundColor, RoundedCornerShape(8.dp))
            .border(1.7.dp, borderColor, RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(16.dp)
            )
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(8.dp))
        }
        Text(
            text = text,
            color = contentColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DepartmentFilterRowPreview() {
    JyPSTheme {
        DepartmentFilterRow(
            searchQuery = "",
            onSearchQueryChange = {},
            selectedFilter = "Activos",
            onFilterChange = {}
        )
    }
}
