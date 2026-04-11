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
import mx.edu.utez.jyps.ui.components.common.EmployeeModeBanner

/**
 * Core dashboard interface for Employee personas.
 * 
 * Provides centralized access to the primary operational workflows: requesting exit passes 
 * and submitting justifications. Includes role-redirection logic for cross-role 
 * functional testing ("Employee Mode").
 *
 * @param onLogoutClick Callback to terminate the current session.
 * @param onHistoryClick Navigation callback for the request status log.
 * @param onProfileClick Navigation callback for the account settings and profile view.
 * @param onRequestPassClick Action to initiate a new exit pass credential request.
 * @param onRequestJustificationClick Action to initiate a new absence justification request.
 * @param userName The full display name of the authenticated employee.
 * @param userEmail Institutional contact email associated with the session.
 * @param showEmployeeModeBanner Toggle to display the administrative role-impersonation banner.
 * @param onReturnToRoleDashboard Action to exit the impersonation mode and return to the primary role dashboard.
 */
@Composable
fun EmployeeDashboardScreen(
    onLogoutClick: () -> Unit = {},
    onHistoryClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onRequestPassClick: () -> Unit = {},
    onRequestJustificationClick: () -> Unit = {},
    userName: String = "Usuario",
    userEmail: String = "",
    showEmployeeModeBanner: Boolean = false,
    onReturnToRoleDashboard: () -> Unit = {}
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
            if (showEmployeeModeBanner) {
                EmployeeModeBanner(onBackClick = onReturnToRoleDashboard)
            }
            
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
