package mx.edu.utez.jyps.ui.components.login

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import mx.edu.utez.jyps.ui.theme.JyPSTheme
import mx.edu.utez.jyps.ui.components.common.BulletText

/**
 * View displayed when a user exceeds their login attempts.
 * Shows a red warning and a countdown timer.
 *
 * @param modifier Standard layout modifier.
 * @param lockoutDurationSeconds Time penalty forced on the user out of actions.
 */
@Composable
fun LockedOutView(
    modifier: Modifier = Modifier,
    lockoutDurationSeconds: Int = 60
) {
    var timer by remember { mutableIntStateOf(lockoutDurationSeconds) }

    LaunchedEffect(key1 = timer) {
        if (timer > 0) {
            delay(1000L)
            timer--
        }
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Red Icon Wrapper
        Box(
            modifier = Modifier
                .size(64.dp)
                .background(Color(0xFFFFE2E2), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Warning,
                contentDescription = "Bloqueado",
                tint = Color(0xFFDC3545),
                modifier = Modifier.size(32.dp)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Cuenta Bloqueada Temporalmente",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFDC3545),
                textAlign = TextAlign.Center
            )

            Text(
                text = "Ha alcanzado el límite de 3 intentos fallidos de inicio de sesión.",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )
        }

        // Timer Box
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFFEF2F2), RoundedCornerShape(8.dp))
                .border(1.7f.dp, Color(0xFFFFC9C9), RoundedCornerShape(8.dp))
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Warning, // Can map to Shield/Stop icon later
                    contentDescription = null,
                    tint = Color(0xFFE7000B),
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "Tu cuenta estará bloqueada por:",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Text(
                text = "1 minuto", // Hardcoded per user request, wait 1 minute
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFDC3545)
            )

            Text(
                text = "Podrás volver a intentar iniciar sesión después de este tiempo.",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }

        // Help Box
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFEFF6FF), RoundedCornerShape(8.dp))
                .border(1.dp, Color(0xFFBEDBFF), RoundedCornerShape(8.dp))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "ℹ️ ¿Qué puedo hacer?",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                BulletText("Espere a que el contador llegue a cero")
                BulletText("Verifique que está usando las credenciales correctas")
                BulletText("Si olvidó su contraseña, use la opción de recuperación")
                BulletText("Contacte al administrador si necesita ayuda")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LockedOutViewPreview() {
    JyPSTheme {
        Box(modifier = Modifier.padding(24.dp)) {
            LockedOutView()
        }
    }
}
