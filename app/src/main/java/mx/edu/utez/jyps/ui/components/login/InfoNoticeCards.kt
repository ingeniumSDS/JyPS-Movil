package mx.edu.utez.jyps.ui.components.login

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mx.edu.utez.jyps.ui.theme.JyPSTheme

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

@Composable
private fun NoticeCard(
    text: String,
    actionText: String,
    icon: ImageVector,
    borderColor: Color,
    iconColor: Color,
    textColor: Color,
    actionColor: Color,
    onActionClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(8.dp),
                ambientColor = Color(0x14000000),
                spotColor = Color(0x14000000)
            )
            .background(Color.White, RoundedCornerShape(8.dp))
            .border(1.dp, borderColor, RoundedCornerShape(8.dp))
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(20.dp)
        )
        
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = text,
                fontSize = 12.sp,
                fontWeight = if (iconColor == Color(0xFFE17100)) FontWeight.Bold else FontWeight.Normal, // Make security text bold
                color = textColor,
                lineHeight = 16.sp
            )
            
            Text(
                text = actionText,
                fontSize = 12.sp,
                color = actionColor,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.clickable { onActionClick() }
            )
        }
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
