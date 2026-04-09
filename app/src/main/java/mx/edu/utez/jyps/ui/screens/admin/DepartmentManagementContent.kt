package mx.edu.utez.jyps.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mx.edu.utez.jyps.ui.components.buttons.PrimaryButton
import mx.edu.utez.jyps.ui.components.admin.DepartmentCard
import mx.edu.utez.jyps.ui.components.admin.DepartmentFilterRow
import mx.edu.utez.jyps.ui.components.admin.DepartmentStatCard
import mx.edu.utez.jyps.ui.theme.JyPSTheme
import mx.edu.utez.jyps.viewmodel.DepartmentUiState

/**
 * The stateless content for the Department Management screen.
 *
 * @param uiState Current visual state derived from the ViewModel.
 * @param onSearchQueryChange Callback for search text updates.
 * @param onFilterChange Callback for filter selection changes.
 * @param onAddClick Callback when the "Crear Departamento" button is pressed.
 * @param onEditClick Callback for editing a specific department.
 * @param onToggleStatusClick Callback for changing a department's active state.
 */
@Composable
fun DepartmentManagementContent(
    uiState: DepartmentUiState,
    onSearchQueryChange: (String) -> Unit,
    onFilterChange: (String) -> Unit,
    onAddClick: () -> Unit,
    onEditClick: (Long) -> Unit,
    onToggleStatusClick: (Long) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Gestión de Departamentos",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F2C59)
                )
                Text(
                    text = "Administrar departamentos de la institución",
                    fontSize = 14.sp,
                    color = Color(0xFF4A5565)
                )
                
                Spacer(modifier = Modifier.height(16.dp))

                PrimaryButton(
                    text = "Crear Departamento",
                    onClick = onAddClick,
                    icon = Icons.Default.Add,
                    // Note: PrimaryButton usually has a default color, but we can override or use theme
                    backgroundColor = Color(0xFF28A745)
                )
            }
        }

        // Stats
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                DepartmentStatCard(
                    title = "Total Departamentos",
                    count = uiState.totalCount,
                    icon = Icons.Default.Business,
                    iconBackground = Color(0xFF0F2C59).copy(alpha = 0.1f),
                    isCentered = true
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    DepartmentStatCard(
                        title = "Activos",
                        count = uiState.activeCount,
                        icon = Icons.Default.CheckCircle,
                        iconBackground = Color(0xFFDCFCE7),
                        textColor = Color(0xFF00A63E),
                        modifier = Modifier.weight(1f)
                    )
                    DepartmentStatCard(
                        title = "Inactivos",
                        count = uiState.inactiveCount,
                        icon = Icons.Default.Cancel,
                        iconBackground = Color(0xFFFFE2E2),
                        textColor = Color(0xFFE7000B),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Filters and Search
        item {
            DepartmentFilterRow(
                searchQuery = uiState.searchQuery,
                onSearchQueryChange = onSearchQueryChange,
                selectedFilter = uiState.selectedFilter,
                onFilterChange = onFilterChange
            )
        }

        // Department List
        items(uiState.departments, key = { it.id }) { department ->
            DepartmentCard(
                departamento = department,
                onEdit = { onEditClick(department.id) },
                onToggleStatus = { onToggleStatusClick(department.id) }
            )
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "© 2026 UTEZ. Sistema JyPS v1.0.0",
                fontSize = 12.sp,
                color = Color(0xFF6A7282)
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Preview(showBackground = true, device = "spec:width=411dp,height=891dp")
@Composable
fun DepartmentManagementContentPreview() {
    JyPSTheme {
        DepartmentManagementContent(
            uiState = DepartmentUiState(
                totalCount = 8,
                activeCount = 7,
                inactiveCount = 1
            ),
            onSearchQueryChange = {},
            onFilterChange = {},
            onAddClick = {},
            onEditClick = {},
            onToggleStatusClick = {}
        )
    }
}
