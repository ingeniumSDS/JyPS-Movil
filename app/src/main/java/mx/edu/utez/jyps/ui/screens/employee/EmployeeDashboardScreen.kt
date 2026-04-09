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

/**
 * Primary dashboard view for Employees.
 * Offers quick actions to request exit passes and justifications, displaying user context.
 *
 * @param onLogoutClick Callback to end the user session.
 * @param onHistoryClick Callback to navigate to the request history screen.
 * @param onProfileClick Callback to navigate to the user's profile view.
 * @param onRequestPassClick Callback to navigate to the exit pass creation screen.
 * @param onRequestJustificationClick Callback to navigate to the justification creation screen.
 * @param userName Full name of the currently authenticated employee.
 * @param userEmail Institutional email of the currently authenticated employee.
 */
@Composable
fun EmployeeDashboardScreen(
    onLogoutClick: () -> Unit = {},
    onHistoryClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onRequestPassClick: () -> Unit = {},
    onRequestJustificationClick: () -> Unit = {},
    userName: String = "Juan",
    userEmail: String = "juan.perez@utez.edu.mx"
) {
    Scaffold(
        topBar = { EmployeeHeader(userName = userName.split(" ").firstOrNull() ?: "User", onLogoutClick = onLogoutClick) },
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
            WelcomeHeroCard(name = userName, email = userEmail)
            
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
