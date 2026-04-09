package mx.edu.utez.jyps.ui.components.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Visual pill styling for user's assigned department.
 *
 * @param dept The department textual name.
 */
@Composable
fun DeptPill(dept: String) {
    Row(
        modifier = Modifier.background(Color(0xFFF3F4F6), RoundedCornerShape(12.dp)).padding(horizontal = 12.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.Business, contentDescription = null, tint = Color(0xFF364153), modifier = Modifier.size(12.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = dept, color = Color(0xFF364153), fontSize = 12.sp)
    }
}

@Preview
@Composable
fun DeptPillPreview() {
    DeptPill(dept = "Ingeniería")
}
