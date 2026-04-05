package mx.edu.utez.jyps.ui.screens.employee

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import mx.edu.utez.jyps.ui.components.cards.DashboardActionCard
import mx.edu.utez.jyps.ui.components.cards.InfoCard
import mx.edu.utez.jyps.ui.components.cards.WelcomeHeroCard
import mx.edu.utez.jyps.ui.components.navigation.AppBottomNavigationBar
import mx.edu.utez.jyps.ui.components.header.EmployeeHeader

@Composable
fun EmployeeDashboardScreen(
    onLogoutClick: () -> Unit = {},
    onHistoryClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onRequestPassClick: () -> Unit = {},
    onRequestJustificationClick: () -> Unit = {}
) {
    Scaffold(
        topBar = { EmployeeHeader(userName = "Juan", onLogoutClick = onLogoutClick) },
        bottomBar = { 
            AppBottomNavigationBar(
                selectedRoute = "inicio", 
                onHistoryClick = onHistoryClick,
                onProfileClick = onProfileClick
            ) 
        },
        containerColor = Color(0xFFF8F9FA)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            WelcomeHeroCard(name = "Juan Pérez García", email = "juan.perez@utez.edu.mx")
            
            DashboardActionCard(
                title = "Pase de Salida",
                description = "Solicita un pase para salir durante el horario escolar",
                icon = Icons.Default.MeetingRoom,
                iconColor = MaterialTheme.colorScheme.primary,
                onClick = onRequestPassClick
            )
            
            DashboardActionCard(
                title = "Justificante",
                description = "Justifica una falta o inasistencia",
                icon = Icons.Default.Description,
                iconColor = Color(0xFF28A745),
                onClick = onRequestJustificationClick
            )
            
            InfoCard()
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun EmployeeDashboardPreview() {
    EmployeeDashboardScreen()
}
