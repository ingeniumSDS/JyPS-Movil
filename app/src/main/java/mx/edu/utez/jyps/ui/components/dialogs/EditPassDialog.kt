package mx.edu.utez.jyps.ui.components.dialogs

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mx.edu.utez.jyps.ui.components.cards.HistoryItem
import mx.edu.utez.jyps.ui.components.inputs.AppTextField
import androidx.compose.foundation.layout.Arrangement
import java.time.LocalTime
import java.time.format.DateTimeFormatter

/**
 * Specialized internal view slot for AppFormDialog handling specific Pass limits.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPassDialog(
    item: HistoryItem,
    onDismissRequest: () -> Unit,
    onSave: (String, String) -> Unit
) {
    var details by remember { mutableStateOf(item.description) }
    var timeRaw by remember { mutableStateOf(item.time) }
    var showTimePicker by remember { mutableStateOf(false) }

    val detailsMinLimit = 25
    val detailsLimit = 255
    val isValid = details.length in detailsMinLimit..detailsLimit

    if (showTimePicker) {
        val timePickerState = rememberTimePickerState()
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val formatted = LocalTime.of(timePickerState.hour, timePickerState.minute)
                        .format(DateTimeFormatter.ofPattern("hh:mm a"))
                    timeRaw = formatted
                    showTimePicker = false
                }) { Text("Aceptar") }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) { Text("Cancelar") }
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    TimePicker(state = timePickerState)
                }
            }
        )
    }

    AppFormDialog(
        title = "Editar Pase de Salida",
        onDismissRequest = onDismissRequest,
        onCancel = onDismissRequest,
        onSave = { onSave(details, timeRaw) },
        isSaveEnabled = isValid
    ) {
        AppTextField(
            label = "Fecha",
            value = item.date,
            onValueChange = {},
            enabled = false
        )

        Box {
            AppTextField(
                label = "Hora de Salida",
                value = timeRaw,
                onValueChange = {},
                readOnly = true,
                placeholder = "--:--",
                trailingIcon = { Icon(Icons.Default.Schedule, tint = Color(0xFF6A7282), contentDescription = null) },
                onClick = { showTimePicker = true }
            )
        }

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
