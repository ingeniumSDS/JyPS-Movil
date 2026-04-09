package mx.edu.utez.jyps.ui.components.departmenthead

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Reusable row displaying a predefined reason with a radio button.
 *
 * @param reason The descriptive text of the rejection reason.
 * @param isSelected Indicates if this reason is the active selection.
 * @param onClick Triggered when the user taps on the row or radio button.
 * @param modifier Optional Compose layout adjustments.
 */
@Composable
fun RejectReasonRow(
    reason: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        RadioButton(
            selected = isSelected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(
                selectedColor = MaterialTheme.colorScheme.primary
            )
        )
        Text(
            text = reason,
            fontSize = 14.sp,
            color = Color(0xFF374151),
            modifier = Modifier.padding(start = 4.dp)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun RejectReasonRowPreview() {
    RejectReasonRow(
        reason = "Información incompleta",
        isSelected = true,
        onClick = {}
    )
}
