package mx.edu.utez.jyps.ui.screens.employee

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun EmployeeDashboardScreen(onLogoutClick: () -> Unit = {}) {
    Scaffold(
        topBar = { EmployeeHeader(userName = "Juan", onLogoutClick = onLogoutClick) },
        bottomBar = { AppBottomNavigation(selectedRoute = "inicio") },
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
                iconColor = Color(0xFF0F2C59),
                onClick = {}
            )
            DashboardActionCard(
                title = "Justificante",
                description = "Justifica una falta o inasistencia",
                icon = Icons.Default.Description,
                iconColor = Color(0xFF28A745),
                onClick = {}
            )
            InfoCard()
        }
    }
}

@Composable
fun EmployeeHeader(userName: String, onLogoutClick: () -> Unit) {
    Surface(color = Color(0xFF0F2C59), shadowElevation = 8.dp) {
        Row(
            modifier = Modifier.fillMaxWidth().statusBarsPadding().padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Sistema JyPS", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text("Bienvenido, $userName", color = Color(0xFFE5E7EB), fontSize = 12.sp)
            }
            IconButton(onClick = onLogoutClick) {
                Icon(Icons.Default.ExitToApp, "Logout", tint = Color.White)
            }
        }
    }
}

@Composable
fun WelcomeHeroCard(name: String, email: String) {
    Box(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp))
            .background(Brush.verticalGradient(listOf(Color(0xFF0F2C59), Color(0xFF1A4178))))
            .padding(16.dp)
    ) {
        Column {
            Text("¡Bienvenido de nuevo!", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text(name, color = Color.White.copy(0.9f), fontSize = 14.sp)
            Text(email, color = Color.White.copy(0.7f), fontSize = 12.sp)
        }
    }
}

@Composable
fun DashboardActionCard(title: String, description: String, icon: ImageVector, iconColor: Color, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(24.dp), 
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(Modifier.size(64.dp).clip(CircleShape).background(iconColor), Alignment.Center) {
                Icon(icon, null, Modifier.size(32.dp), Color.White)
            }
            Spacer(Modifier.height(12.dp))
            Text(title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F2C59), textAlign = TextAlign.Center)
            Text(description, fontSize = 14.sp, color = Color(0xFF4A5565), textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun InfoCard() {
    Card(Modifier.fillMaxWidth(), RoundedCornerShape(8.dp), CardDefaults.cardColors(Color.White), CardDefaults.cardElevation(2.dp)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("ℹ️ Información Importante", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF0F2C59))
            listOf(
                "• Las solicitudes deben ser aprobadas por un admin",
                "• Tu código QR será activado una vez aprobada tu solicitud",
                "• Presenta el QR al personal de seguridad al salir",
                "• Revisa tu historial para ver el estado de tus solicitudes"
            ).forEach { Text(it, fontSize = 12.sp, color = Color(0xFF4A5565)) }
        }
    }
}

@Composable
fun AppBottomNavigation(selectedRoute: String) {
    NavigationBar(containerColor = Color.White, tonalElevation = 8.dp) {
        NavigationBarItem(selectedRoute == "inicio", {}, { Icon(Icons.Default.Home, null) }, label = { Text("Inicio") })
        NavigationBarItem(selectedRoute == "historial", {}, { Icon(Icons.Default.History, null) }, label = { Text("Historial") })
        NavigationBarItem(selectedRoute == "perfil", {}, { Icon(Icons.Default.Person, null) }, label = { Text("Perfil") })
    }
}

@Preview(showSystemUi = true)
@Composable
fun EmployeeDashboardPreview() { EmployeeDashboardScreen() }
