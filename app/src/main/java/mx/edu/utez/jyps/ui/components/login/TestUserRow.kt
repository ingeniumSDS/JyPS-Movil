package mx.edu.utez.jyps.ui.components.login

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import mx.edu.utez.jyps.ui.theme.JyPSTheme

/**
 * Text row presenting mock user credentials for testing.
 *
 * @param role The logical representation of the employee access.
 * @param email The target email to type for sign in.
 * @param isError Flashes text as red if the account tests an invalid state constraint.
 */
@Composable
fun TestUserRow(
    role: String,
    email: String,
    isError: Boolean = false
) {
    Text(
        text = "• $role: $email",
        fontSize = 12.sp,
        color = if (isError) Color(0xFFE7000B) else Color(0xFF6A7282),
        lineHeight = 16.sp
    )
}

@Preview(showBackground = true)
@Composable
fun TestUserRowPreview() {
    JyPSTheme {
        TestUserRow("Director", "director@utez.edu.mx")
    }
}
