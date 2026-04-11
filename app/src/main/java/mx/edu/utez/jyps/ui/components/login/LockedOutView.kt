package mx.edu.utez.jyps.ui.components.login

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import mx.edu.utez.jyps.ui.components.buttons.PrimaryButton

/**
 * Resilient view displayed when access is prohibited to enforce security policies.
 * 
 * Supports two distinct security modes:
 * 1. Local Lockout: Triggered after consecutive failed attempts, features a client-side countdown.
 * 2. Server Block: Determined by the backend (identity provider), presents a static error message.
 *
 * @param modifier Applied to the root container for layout customization.
 * @param isServerSide Flag to toggle between local countdown and static server-side rejection UI.
 * @param serverMessage Descriptive error provided by the security service for blocked accounts.
 * @param lockoutDurationSeconds The time penalty in seconds for local anti-bruteforce measures.
 * @param onReturnToLogin Action to reset the UI state and allow the user to retry or switch accounts.
 */
@Composable
fun LockedOutView(
    modifier: Modifier = Modifier,
    isServerSide: Boolean = false,
    serverMessage: String? = null,
    lockoutDurationSeconds: Int = 60,
    onReturnToLogin: () -> Unit = {}
) {
    var timer by remember { mutableIntStateOf(lockoutDurationSeconds) }

    LaunchedEffect(key1 = timer) {
        if (!isServerSide && timer > 0) {
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
                imageVector = if (isServerSide) Icons.Filled.Block else Icons.Filled.Warning,
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
                text = if (isServerSide) "Cuenta Bloqueada" else "Acceso Restringido",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFDC3545),
                textAlign = TextAlign.Center
            )

            Text(
                text = if (isServerSide) 
                    "La cuenta está bloqueada temporalmente por seguridad." 
                    else "Ha alcanzado el límite de 3 intentos fallidos.",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )
        }

        // Timer or Message Box
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFFEF2F2), RoundedCornerShape(8.dp))
                .border(1.dp, Color(0xFFFFC9C9), RoundedCornerShape(8.dp))
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Info,
                    contentDescription = null,
                    tint = Color(0xFFE7000B),
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = if (isServerSide) "Información del servidor:" else "Tiempo de espera:",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            if (isServerSide) {
                Text(
                    text = serverMessage ?: "Acceso denegado debido a múltiples intentos fallidos. Inténtelo más adelante.",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp
                )
            } else {
                Text(
                    text = "1 minuto",
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
        }

        PrimaryButton(
            text = "Regresar al Inicio de Sesión",
            onClick = onReturnToLogin,
            modifier = Modifier.fillMaxWidth()
        )

        if (!isServerSide) {
            // Help Box for Local Lockout
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFEFF6FF), RoundedCornerShape(8.dp))
                    .border(1.dp, Color(0xFFBEDBFF), RoundedCornerShape(8.dp))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "ℹ️ ¿Qué puedo hacer?",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    BulletText("Espere a que el contador llegue a cero")
                    BulletText("Verifique sus credenciales")
                    BulletText("Use la opción de recuperación de contraseña")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LockedOutViewPreview() {
    JyPSTheme {
        Box(modifier = Modifier.padding(24.dp)) {
            LockedOutView(isServerSide = true, serverMessage = "Ejemplo de bloqueo de servidor")
        }
    }
}
