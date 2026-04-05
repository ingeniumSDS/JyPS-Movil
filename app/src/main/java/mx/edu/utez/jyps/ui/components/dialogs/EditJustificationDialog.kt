package mx.edu.utez.jyps.ui.components.dialogs

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mx.edu.utez.jyps.ui.components.cards.HistoryItem
import mx.edu.utez.jyps.ui.components.inputs.AppTextField
import androidx.compose.foundation.layout.Arrangement

/**
 * Specialized internal view slot for AppFormDialog handling specific Justification limits.
 */
@Composable
fun EditJustificationDialog(
    item: HistoryItem,
    onDismissRequest: () -> Unit,
    onSave: (String) -> Unit
) {
    var details by remember { mutableStateOf(item.description) }

    val detailsMinLimit = 25
    val detailsLimit = 255
    val isValid = details.length in detailsMinLimit..detailsLimit

    AppFormDialog(
        title = "Editar Justificante",
        onDismissRequest = onDismissRequest,
        onCancel = onDismissRequest,
        onSave = { onSave(details) },
        isSaveEnabled = isValid
    ) {
        AppTextField(
            label = "Fecha",
            value = item.date,
            onValueChange = {},
            enabled = false
        )

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Row {
                    Text("Motivo ", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color(0xFF364153))
                    Text("*", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color.Red)
                }
                Text(
                    "${details.length}/$detailsLimit",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (!isValid) Color.Red else Color(0xFF6A7282)
                )
            }
            
            OutlinedTextField(
                value = details,
                onValueChange = { if (it.length <= detailsLimit) details = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = Color(0xFFD1D5DC)
                )
            )
        }
    }
}
