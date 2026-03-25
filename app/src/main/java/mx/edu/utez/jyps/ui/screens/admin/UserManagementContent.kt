package mx.edu.utez.jyps.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.PersonOff
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import mx.edu.utez.jyps.data.repository.LoadResult
import mx.edu.utez.jyps.ui.components.admin.MetricCard
import mx.edu.utez.jyps.ui.components.admin.UserCard
import mx.edu.utez.jyps.ui.components.admin.UserFilterRow
import mx.edu.utez.jyps.viewmodel.AdminViewModel

@Composable
fun UserManagementContent(viewModel: AdminViewModel) {
    val users by viewModel.users.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedFilter by viewModel.selectedFilter.collectAsStateWithLifecycle()
    val loadState by viewModel.loadState.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoadingUsers.collectAsStateWithLifecycle()
    val accountStatuses by viewModel.accountStatuses.collectAsStateWithLifecycle()

    // Calculate metrics using account statuses
    val totalUsers = users.size
    val activeUsers = users.count { accountStatuses[it.id]?.activa ?: true }
    val inactiveUsers = users.count { !(accountStatuses[it.id]?.activa ?: true) }

    // Filter logic
    val filteredUsers = users.filter { user ->
        val isActivo = accountStatuses[user.id]?.activa ?: true

        val matchesSearch = user.nombreCompleto.contains(searchQuery, ignoreCase = true) ||
                            user.correo.contains(searchQuery, ignoreCase = true) ||
                            user.primaryRoleDisplay.contains(searchQuery, ignoreCase = true)

        val matchesFilter = when (selectedFilter) {
            "Activos" -> isActivo
            "Inactivos" -> !isActivo
            else -> true
        }

        matchesSearch && matchesFilter
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text("Gestión de Usuarios", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F2C59))
                Text("Administrar usuarios del sistema", fontSize = 16.sp, color = Color(0xFF4A5565), modifier = Modifier.padding(top = 4.dp, bottom = 16.dp))
                Button(
                    onClick = { viewModel.setCreateUserVisible(true) },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF28A745)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
                    Spacer(Modifier.width(8.dp))
                    Text("Crear Usuario", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                }
            }
        }

        // Metrics
        item {
            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                MetricCard(
                    title = "Total Usuarios", value = totalUsers.toString(),
                    icon = Icons.Default.Group,
                    iconContainerColor = Color(0xFFF3F4F6), contentColor = Color(0xFF0F2C59)
                )
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    MetricCard(
                        title = "Usuarios Activos", value = activeUsers.toString(),
                        icon = Icons.Default.PersonOutline,
                        iconContainerColor = Color(0xFFDCFCE7), contentColor = Color(0xFF28A745),
                        modifier = Modifier.weight(1f),
                        isVertical = true
                    )
                    MetricCard(
                        title = "Usuarios Inactivos", value = inactiveUsers.toString(),
                        icon = Icons.Default.PersonOff,
                        iconContainerColor = Color(0xFFFFE2E2), contentColor = Color(0xFFDC3545),
                        modifier = Modifier.weight(1f),
                        isVertical = true
                    )
                }
            }
        }

        // Filters
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

        // Loading state
        if (isLoading) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(48.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF0F2C59))
                }
            }
        }
        // Error state
        else if (loadState is LoadResult.Error) {
            item {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(48.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(Icons.Default.CloudOff, contentDescription = null, tint = Color(0xFFDC3545), modifier = Modifier.size(48.dp))
                    Text("Error de conexión", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF0F2C59))
                    Text(
                        (loadState as LoadResult.Error).message,
                        fontSize = 14.sp, color = Color(0xFF6A7282), textAlign = TextAlign.Center
                    )
                    TextButton(onClick = { viewModel.loadUsuarios() }) {
                        Text("Reintentar", color = Color(0xFF0F2C59), fontWeight = FontWeight.Medium)
                    }
                }
            }
        }
        // Empty state
        else if (users.isEmpty() && loadState is LoadResult.Success) {
            item {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(48.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(Icons.Default.SearchOff, contentDescription = null, tint = Color(0xFF99A1AF), modifier = Modifier.size(48.dp))
                    Text("No hay usuarios registrados", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF0F2C59))
                    Text("Crea un nuevo usuario para comenzar.", fontSize = 14.sp, color = Color(0xFF6A7282))
                }
            }
        }
        // User List
        else {
            if (filteredUsers.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(48.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(Icons.Default.SearchOff, contentDescription = null, tint = Color(0xFF99A1AF), modifier = Modifier.size(48.dp))
                        Text("Sin resultados", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF0F2C59))
                        Text("No se encontraron usuarios que coincidan con tu búsqueda.", fontSize = 14.sp, color = Color(0xFF6A7282), textAlign = TextAlign.Center)
                    }
                }
            } else {
                items(filteredUsers) { usuario ->
                    UserCard(
                        usuario = usuario,
                        cuenta = accountStatuses[usuario.id],
                        onEditClick = { viewModel.openEditUser(it) },
                        onToggleStatusClick = { viewModel.toggleUserStatus(it) },
                        onViewDetail = { userId -> viewModel.viewUserDetail(userId) }
                    )
                }
            }
        }
    }
}
