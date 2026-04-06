package mx.edu.utez.jyps.ui.components.departmenthead

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Represents one selectable filter option with its display label and current count.
 *
 * @property label User-visible text (e.g. "Pendientes").
 * @property count Number to append in parentheses.
 * @property key Programmatic identifier matching [mx.edu.utez.jyps.data.model.RequestStatus] or ALL.
 */
data class FilterOption(
    val label: String,
    val count: Int,
    val key: String
)

/**
 * Row of Material 3 [FilterChip]s arranged in a wrapping flow layout.
 * The Figma design shows 2 rows of 2 chips each.
 *
 * @param options List of filter options to render.
 * @param selectedKey Currently active filter key.
 * @param onFilterSelected Callback with the key of the tapped chip.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FilterChipRow(
    options: List<FilterOption>,
    selectedKey: String,
    onFilterSelected: (String) -> Unit
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        options.forEach { option ->
            val isSelected = option.key == selectedKey

            FilterChip(
                selected = isSelected,
                onClick = { onFilterSelected(option.key) },
                label = {
                    Text(
                        text = "${option.label} (${option.count})",
                        fontSize = 13.sp
                    )
                },
                shape = RoundedCornerShape(20.dp),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = Color.White,
                    containerColor = Color(0xFFF3F4F6),
                    labelColor = Color(0xFF374151)
                ),
                border = FilterChipDefaults.filterChipBorder(
                    borderColor = Color(0xFFD1D5DC),
                    selectedBorderColor = MaterialTheme.colorScheme.primary,
                    enabled = true,
                    selected = isSelected
                ),
                modifier = Modifier.padding(0.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FilterChipRowPreview() {
    FilterChipRow(
        options = listOf(
            FilterOption("Todos", 38, "ALL"),
            FilterOption("Pendientes", 13, "PENDING"),
            FilterOption("Aprobadas", 22, "APPROVED"),
            FilterOption("Rechazadas", 2, "REJECTED")
        ),
        selectedKey = "ALL",
        onFilterSelected = {}
    )
}
