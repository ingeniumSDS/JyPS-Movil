package mx.edu.utez.jyps.ui.components.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.compose.ui.tooling.preview.Preview

/**
 * Navigation and filter selection row combining search and pill filtering.
 *
 * @param searchQuery Tracks the current input text inside the search field.
 * @param onSearchQueryChange Callback triggered upon user typing.
 * @param selectedFilter String identification of the currently active filter pill.
 * @param onFilterSelect Callback triggered when a filter pill is pressed.
 */
@Composable
fun UserFilterRow(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    selectedFilter: String,
    onFilterSelect: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            placeholder = { 
                Text(
                    "Buscar por nombre, email o rol...", 
                    color = Color.Gray.copy(alpha = 0.8f),
                    fontSize = 16.sp
                ) 
            },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = "Buscar", tint = Color.Gray)
            },
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF0F2C59),
                unfocusedBorderColor = Color(0xFFD1D5DC),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Filter Pills
        val filters = listOf("Todos", "Activos", "Inactivos")
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filters) { filter ->
                FilterPill(
                    text = filter,
                    isSelected = selectedFilter == filter,
                    onClick = { onFilterSelect(filter) }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UserFilterRowPreview() {
    UserFilterRow(searchQuery = "Carlos", onSearchQueryChange = {}, selectedFilter = "Todos", onFilterSelect = {})
}
