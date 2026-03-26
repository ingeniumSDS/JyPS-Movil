package mx.edu.utez.jyps.ui.components.scanner

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.QrCode
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
import mx.edu.utez.jyps.viewmodel.ScannerTab

/**
 * Top tabs to toggle between "Scan QR" and "Manual Code".
 *
 * Displays the selected tab with consistent colors (dark background and white text).
 *
 * @param selectedTab Currently active tab.
 * @param onTabSelected Callback triggered when a tab is tapped.
 * @param modifier Optional modifier.
 */
@Composable
fun ScannerTabs(
    selectedTab: ScannerTab,
    onTabSelected: (ScannerTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Scan QR Tab
        val isQrSelected = selectedTab == ScannerTab.QR
        Row(
            modifier = Modifier
                .weight(1f)
                .height(40.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(if (isQrSelected) Color(0xFF0F2C59) else Color.White)
                .border(
                    width = 1.dp,
                    color = if (isQrSelected) Color.Transparent else Color(0xFFE5E7EB),
                    shape = RoundedCornerShape(8.dp)
                )
                .clickable { onTabSelected(ScannerTab.QR) },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.QrCode,
                contentDescription = null,
                tint = if (isQrSelected) Color.White else Color(0xFF364153),
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Scan QR",
                color = if (isQrSelected) Color.White else Color(0xFF364153),
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp
            )
        }

        // Manual Code Tab
        val isManualSelected = selectedTab == ScannerTab.MANUAL
        Row(
            modifier = Modifier
                .weight(1f)
                .height(40.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(if (isManualSelected) Color(0xFF0F2C59) else Color.White)
                .border(
                    width = 1.dp,
                    color = if (isManualSelected) Color.Transparent else Color(0xFFE5E7EB),
                    shape = RoundedCornerShape(8.dp)
                )
                .clickable { onTabSelected(ScannerTab.MANUAL) },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Keyboard,
                contentDescription = null,
                tint = if (isManualSelected) Color.White else Color(0xFF364153),
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Manual Code",
                color = if (isManualSelected) Color.White else Color(0xFF364153),
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp
            )
        }
    }
}

@Preview
@Composable
fun ScannerTabsPreview() {
    Column {
        ScannerTabs(selectedTab = ScannerTab.QR, onTabSelected = {})
        Spacer(Modifier.height(8.dp))
        ScannerTabs(selectedTab = ScannerTab.MANUAL, onTabSelected = {})
    }
}
