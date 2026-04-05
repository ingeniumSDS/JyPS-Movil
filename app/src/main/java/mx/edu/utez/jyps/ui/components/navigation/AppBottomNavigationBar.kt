package mx.edu.utez.jyps.ui.components.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import mx.edu.utez.jyps.ui.theme.JyPSTheme

/**
 * Reusable bottom navigation bar for the application.
 * 
 * @param selectedRoute The current active route (inicio, historial, perfil).
 * @param onHomeClick Navigation callback for Home.
 * @param onHistoryClick Navigation callback for History.
 * @param onProfileClick Navigation callback for Profile.
 */
@Composable
fun AppBottomNavigationBar(
    selectedRoute: String,
    onHomeClick: () -> Unit = {},
    onHistoryClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    NavigationBar(containerColor = Color.White, tonalElevation = 8.dp) {
        NavigationBarItem(
            selected = selectedRoute == "inicio",
            onClick = onHomeClick,
            icon = { Icon(Icons.Default.Home, null) },
            label = { Text("Inicio") }
        )
        NavigationBarItem(
            selected = selectedRoute == "historial",
            onClick = onHistoryClick,
            icon = { Icon(Icons.Default.History, null) },
            label = { Text("Historial") }
        )
        NavigationBarItem(
            selected = selectedRoute == "perfil",
            onClick = onProfileClick,
            icon = { Icon(Icons.Default.Person, null) },
            label = { Text("Perfil") }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AppBottomNavigationBarPreview() {
    JyPSTheme {
        AppBottomNavigationBar(selectedRoute = "inicio", onHomeClick = {}, onHistoryClick = {}, onProfileClick = {})
    }
}
