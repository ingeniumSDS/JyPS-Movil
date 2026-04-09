package mx.edu.utez.jyps.ui.components.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.DrawerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.ui.unit.dp

/**
 * Data class representing a menu item in the navigation drawer.
 *
 * @property title The display text for the menu option.
 * @property icon Vector icon presented next to the text.
 * @property route Target string key used for navigational matching.
 */
data class DrawerMenuItem(
    val title: String,
    val icon: ImageVector,
    val route: String
)

/**
 * Menu options for the Administrator role.
 */
val adminMenuOptions = listOf(
    DrawerMenuItem("Gestión de Usuarios", Icons.Default.People, "admin_users"),
    DrawerMenuItem("Gestión de Departamentos", Icons.Default.Business, "admin_departments"),
    DrawerMenuItem("Solicitar Pase", Icons.Default.DirectionsWalk, "admin_pass"),
    DrawerMenuItem("Solicitar Justificante", Icons.Default.DocumentScanner, "admin_excuse"),
    DrawerMenuItem("Mi Historial", Icons.Default.History, "admin_history"),
    DrawerMenuItem("Mi Perfil", Icons.Default.Person, "admin_profile")
)

/**
 * Menu options for the Department Head role.
 */
val deptHeadMenuOptions = listOf(
    DrawerMenuItem("Dashboard", Icons.Default.Dashboard, "department_head_dashboard"),
    DrawerMenuItem("Empleados", Icons.Default.People, "dept_employees"),
    DrawerMenuItem("Solicitar Pase", Icons.Default.DirectionsWalk, "pass_request"),
    DrawerMenuItem("Solicitar Justificante", Icons.Default.DocumentScanner, "justification_request"),
    DrawerMenuItem("Mi Historial", Icons.Default.History, "history"),
    DrawerMenuItem("Mi Perfil", Icons.Default.Person, "profile")
)

/**
 * A highly reusable navigation drawer that adapts to different roles and users.
 * Respects corporate Blue and Gold color palette for administrative roles.
 *
 * @param drawerState State controlling the visibility of the drawer.
 * @param menuItems List of items to display in the menu.
 * @param currentRoute The currently active route for highlighting.
 * @param userFullName Full name of the logged-in user.
 * @param userEmail Email of the logged-in user.
 * @param roleTitle Title of the user's role (e.g., "Administrador").
 * @param onNavigateTo Callback invoked when a menu item is clicked.
 * @param onLogout Callback invoked when the logout button is clicked.
 * @param content The main screen content to be displayed alongside the drawer.
 */
@Composable
fun AppNavigationDrawer(
    drawerState: DrawerState,
    menuItems: List<DrawerMenuItem>,
    currentRoute: String,
    userFullName: String,
    userEmail: String,
    roleTitle: String,
    onNavigateTo: (String) -> Unit,
    onLogout: () -> Unit,
    content: @Composable () -> Unit
) {
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = Color(0xFF0F2C59), // Primary Dark Blue
                drawerShape = RoundedCornerShape(topEnd = 0.dp, bottomEnd = 0.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(280.dp)
                ) {
                    // Header with dynamic role title
                    DrawerHeader(roleTitle = roleTitle)
                    
                    HorizontalDivider(color = Color.White.copy(alpha = 0.1f))

                    // Menu Items
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 12.dp, vertical = 24.dp)
                    ) {
                        menuItems.forEach { item ->
                            DrawerItemRow(
                                item = item,
                                isSelected = currentRoute == item.route,
                                onClick = { onNavigateTo(item.route) }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }

                    HorizontalDivider(color = Color.White.copy(alpha = 0.1f))

                    // Footer with dynamic user info
                    DrawerFooter(
                        userFullName = userFullName,
                        userEmail = userEmail,
                        onLogoutClick = onLogout
                    )
                }
            }
        },
        content = content
    )
}

@Preview(showBackground = true)
@Composable
fun AppNavigationDrawerPreview() {
    AppNavigationDrawer(
        drawerState = rememberDrawerState(DrawerValue.Open),
        menuItems = adminMenuOptions,
        currentRoute = "admin_users",
        userFullName = "Juan Perez",
        userEmail = "juan@utez.edu.mx",
        roleTitle = "Administrador",
        onNavigateTo = {},
        onLogout = {},
        content = { Text("App Content Here") }
    )
}
