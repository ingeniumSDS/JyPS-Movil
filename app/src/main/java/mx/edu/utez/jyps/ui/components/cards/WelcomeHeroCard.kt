package mx.edu.utez.jyps.ui.components.cards

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mx.edu.utez.jyps.ui.theme.JyPSTheme

/**
 * Reusable hero card for welcomes.
 *
 * @param name User's full name.
 * @param email User's email address.
 */
@Composable
fun WelcomeHeroCard(name: String, email: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(
                Brush.verticalGradient(
                    listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                    )
                )
            )
            .padding(16.dp)
    ) {
        Column {
            Text(
                "¡Bienvenido de nuevo!",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(name, color = Color.White.copy(0.9f), fontSize = 14.sp)
            Text(email, color = Color.White.copy(0.7f), fontSize = 12.sp)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WelcomeHeroCardPreview() {
    JyPSTheme {
        WelcomeHeroCard(name = "Juan Perez", email = "juan@example.com")
    }
}
