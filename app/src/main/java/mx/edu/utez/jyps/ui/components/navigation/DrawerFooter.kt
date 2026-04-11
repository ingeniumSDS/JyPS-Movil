package mx.edu.utez.jyps.ui.components.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Identity Footer for the navigation drawer.
 * Displays the current session profile (name, email, and avatar initial) and provides
 * a centralized sign-out action to revoke the local session.
 *
 * @param userFullName The full display name retrieved from the identity provider.
 * @param userEmail The institutional email associated with the active session.
 * @param onLogoutClick Callback to trigger the session revocation and data purge flow.
 */
@Composable
fun DrawerFooter(
    userFullName: String,
    userEmail: String,
    onLogoutClick: () -> Unit
) {
    val userInitial = userFullName.firstOrNull()?.toString() ?: "?"

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color(0xFF1E3A5F), RoundedCornerShape(20.dp)), // Secondary Darker Blue
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = userInitial.uppercase(),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = userFullName,
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = userEmail,
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
                contentDescription = null,
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

@Preview
@Composable
fun DrawerFooterPreview() {
    Box(modifier = Modifier.background(Color(0xFF0F2C59))) {
        DrawerFooter("Admin UTEZ", "admin@utez.edu.mx", {})
    }
}
