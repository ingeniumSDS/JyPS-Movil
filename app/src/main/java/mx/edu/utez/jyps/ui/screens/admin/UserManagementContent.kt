package mx.edu.utez.jyps.ui.screens.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.PersonOff
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import mx.edu.utez.jyps.ui.components.admin.MetricCard
import mx.edu.utez.jyps.ui.components.admin.UserCard
import mx.edu.utez.jyps.ui.components.admin.UserFilterRow
import mx.edu.utez.jyps.viewmodel.AdminViewModel

@Composable
fun UserManagementContent(viewModel: AdminViewModel) {
    val users by viewModel.users.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedFilter by viewModel.selectedFilter.collectAsStateWithLifecycle()

    // Calculate metrics
    val totalUsers = users.size
    val activeUsers = users.count { it.cuenta.activa }
    val inactiveUsers = users.count { !it.cuenta.activa }

    // Filter logic
    val filteredUsers = users.filter { user ->
        val matchesSearch = user.usuario.nombreCompleto.contains(searchQuery, ignoreCase = true) ||
                            user.usuario.correo.contains(searchQuery, ignoreCase = true) ||
                            (user.primaryRole?.nombre?.contains(searchQuery, ignoreCase = true) ?: false)
        
        val matchesFilter = when (selectedFilter) {
            "Activos" -> user.cuenta.activa
            "Inactivos" -> !user.cuenta.activa
            else -> true
        }

        matchesSearch && matchesFilter
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // App Title / Subtitle & Create Button
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Gestión de Usuarios",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F2C59)
                )
                Text(
                    text = "Administrar usuarios del sistema",
                    fontSize = 16.sp,
                    color = Color(0xFF4A5565),
                    modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                )

                Button(
                    onClick = { viewModel.setCreateUserVisible(true) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF28A745)),
                    shape = RoundedCornerShape(8.dp)
                ) { Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
                    Spacer(Modifier.width(8.dp))
                    Text("Crear Usuario", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                }
            }
        }

        // Metrics Section
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Row 1
                MetricCard(
                    title = "Total Usuarios",
                    value = totalUsers.toString(),
                    icon = Icons.Default.Group,
                    iconContainerColor = Color(0xFFF3F4F6),
                    contentColor = Color(0xFF0F2C59)
                )
                
                // Row 2
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    MetricCard(
                        title = "Usuarios Activos",
                        value = activeUsers.toString(),
                        icon = Icons.Default.PersonOutline,
                        iconContainerColor = Color(0xFFDCFCE7),
                        contentColor = Color(0xFF28A745),
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        title = "Usuarios Inactivos",
                        value = inactiveUsers.toString(),
                        icon = Icons.Default.PersonOff,
                        iconContainerColor = Color(0xFFFFE2E2),
                        contentColor = Color(0xFFDC3545),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Filters Section
        item {
            Spacer(modifier = Modifier.height(8.dp))
            UserFilterRow(
                searchQuery = searchQuery,
                onSearchQueryChange = viewModel::onSearchQueryChange,
                selectedFilter = selectedFilter,
                onFilterSelect = viewModel::setFilter
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        // User List
        items(filteredUsers) { userWithDetails ->
            UserCard(
                userWithDetails = userWithDetails,
                onEditClick = { /* TODO */ },
                onToggleStatusClick = { /* TODO */ }
            )
        }
    }
}
