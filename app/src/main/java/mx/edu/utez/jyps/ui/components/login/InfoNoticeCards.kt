package mx.edu.utez.jyps.ui.components.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import mx.edu.utez.jyps.ui.theme.JyPSTheme

/**
 * Displays informational and security cards natively rendered.
 *
 * @param onSecurityClick Action trigger.
 * @param onPrivacyClick Action trigger.
 * @param modifier Optional Compose layout adjustments.
 */
@Composable
fun InfoNoticeCards(
    onSecurityClick: () -> Unit,
    onPrivacyClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        NoticeCard(
            text = "Aviso de Seguridad: Este sistema NO está diseñado para recopilar información personal identificable (PII) sensible ni datos confidenciales. No ingrese información médica detallada, datos financieros o información altamente sensible.",
            actionText = "Ver más información de seguridad",
            icon = Icons.Filled.Warning, // Map to an outline stop icon if possible
            borderColor = Color(0xFFFEE685),
            iconColor = Color(0xFFE17100),
            textColor = Color(0xFF973C00),
            actionColor = Color(0xFFBB4D00),
            onActionClick = onSecurityClick
        )

        NoticeCard(
            text = "Al iniciar sesión, aceptas el uso de cookies y almacenamiento local para mantener tu sesión activa.",
            actionText = "Ver Aviso de Privacidad completo",
            icon = Icons.Filled.Info,
            borderColor = Color(0xFFBEDBFF),
            iconColor = Color(0xFF155DFC),
            textColor = Color(0xFF193CB8),
            actionColor = Color(0xFF1447E6),
            onActionClick = onPrivacyClick
        )
    }
}

@Preview(showBackground = true)
@Composable
fun InfoNoticeCardsPreview() {
    JyPSTheme {
        InfoNoticeCards(
            onSecurityClick = {},
            onPrivacyClick = {},
            modifier = Modifier.padding(24.dp)
        )
    }
}
