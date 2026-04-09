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

/**
 * Individual notice card unit encapsulating an informative text and an action hyperlink.
 *
 * @param text The informational message description.
 * @param actionText The hyperlink string text to map an action.
 * @param icon The vector asset representing the notice type.
 * @param borderColor Dynamic color defining the container outlines.
 * @param iconColor Dynamic tint color for the icon.
 * @param textColor Dynamic tint color for the descriptive text.
 * @param actionColor Dynamic tint color for the actionable text.
 * @param onActionClick Triggered when the clickable string is executed.
 */
@Composable
fun NoticeCard(
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
fun NoticeCardPreview() {
    JyPSTheme {
        NoticeCard(
            text = "Sample Info Text Message Block",
            actionText = "Click here to see more",
            icon = Icons.Default.Info,
            borderColor = Color.Gray,
            iconColor = Color.Blue,
            textColor = Color.Black,
            actionColor = Color.Blue,
            onActionClick = {}
        )
    }
}
