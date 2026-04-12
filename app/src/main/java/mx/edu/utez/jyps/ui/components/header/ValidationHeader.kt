package mx.edu.utez.jyps.ui.components.header

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mx.edu.utez.jyps.R

/**
 * Component that renders the 'Header' for the Validation screen.
 *
 * Follows strict separation and single responsibility principles, abstracting
 * the top visual aspects from any complex domain logic.
 *
 * @param userName Name of the logged-in user, e.g., "María González Hernández".
 * @param modifier Optional modifier to inject visual configurations from the parent.
 * @param onLogoutClick Lambda executed when the logout button is pressed.
 */
@Composable
fun ValidationHeader(
    userName: String,
    modifier: Modifier = Modifier,
    onLogoutClick: () -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFF0F2C59)) // Figma dark primary color
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left representative icon
        Box(
            modifier = Modifier
                .size(32.dp)
                .border(2.dp, Color.White, RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.QrCodeScanner,
                contentDescription = stringResource(R.string.app_name),
                tint = Color.White,
                modifier = Modifier.padding(4.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        // Title and user texts
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = stringResource(R.string.scanner_header_title),
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = stringResource(R.string.scanner_guard_prefix, userName),
                color = Color(0xFFE5E7EB), // Light gray color
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal
            )
        }
        
        Spacer(modifier = Modifier.width(8.dp))
        
        // Action button (logout)
        IconButton(
            onClick = onLogoutClick,
            modifier = Modifier
                .size(40.dp)
                .background(Color(0xFF233B63), RoundedCornerShape(8.dp)) // Contrast background
        ) {
            Icon(
                imageVector = Icons.Default.ExitToApp,
                contentDescription = stringResource(R.string.scanner_logout_content_description),
                tint = Color.White
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ValidationHeaderPreview() {
    ValidationHeader(
        userName = "María González Hernández"
    )
}
