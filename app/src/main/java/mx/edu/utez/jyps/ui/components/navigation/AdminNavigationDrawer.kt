package mx.edu.utez.jyps.ui.components.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.DrawerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class DrawerMenuItem(
    val title: String,
    val icon: ImageVector,
    val route: String
)

val adminMenuOptions = listOf(
    DrawerMenuItem("Gestión de Usuarios", Icons.Default.People, "admin_users"),
    DrawerMenuItem("Gestión de Departamentos", Icons.Default.Business, "admin_departments"),
    DrawerMenuItem("Solicitar Pase", Icons.Default.DirectionsWalk, "admin_pass"),
    DrawerMenuItem("Solicitar Justificante", Icons.Default.DocumentScanner, "admin_excuse"),
    DrawerMenuItem("Mi Historial", Icons.Default.History, "admin_history"),
    DrawerMenuItem("Mi Perfil", Icons.Default.Person, "admin_profile")
)

@Composable
fun AdminNavigationDrawer(
    drawerState: DrawerState,
    currentRoute: String,
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
                    // Header
                    DrawerHeader()
                    
                    HorizontalDivider(color = Color.White.copy(alpha = 0.1f))

                    // Menu Items
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 12.dp, vertical = 24.dp)
                    ) {
                        adminMenuOptions.forEach { item ->
                            DrawerItemRow(
                                item = item,
                                isSelected = currentRoute == item.route,
                                onClick = { onNavigateTo(item.route) }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }

                    HorizontalDivider(color = Color.White.copy(alpha = 0.1f))

                    // Footer Profile & Logout
                    DrawerFooter(onLogoutClick = onLogout)
                }
            }
        },
        content = content
    )
}

@Composable
private fun DrawerHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Logo Container
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(Color(0xFFD4AF37), RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            // Placeholder for the actual Shield Icon Vector in Figma
            Icon(
                imageVector = Icons.Default.Business,
                contentDescription = "Logo",
                tint = Color(0xFF0F2C59),
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(
                text = "Sistema JyPS",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Administrador",
                color = Color(0xFFD1D5DC),
                fontSize = 12.sp
            )
        }
    }
}

@Composable
private fun DrawerItemRow(
    item: DrawerMenuItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) Color(0xFFD4AF37) else Color.Transparent
    val contentColor = if (isSelected) Color(0xFF0F2C59) else Color.White

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = item.icon,
            contentDescription = item.title,
            tint = contentColor,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = item.title,
            color = contentColor,
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
        )
    }
}

@Composable
private fun DrawerFooter(onLogoutClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Profile Initial
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(Color.White.copy(alpha = 0.1f), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "C", // Placeholder for "Carlos"
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = "Carlos Rodríguez Torres",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "carlos.rodriguez@utez.edu.mx",
                    color = Color(0xFFD1D5DC),
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .clickable { onLogoutClick() }
                .padding(vertical = 8.dp, horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Logout,
                contentDescription = "Cerrar Sesión",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Cerrar Sesión",
                color = Color.White,
                fontSize = 12.sp
            )
        }
    }
}
