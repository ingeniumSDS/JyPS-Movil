package mx.edu.utez.jyps.ui.components.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.tooling.preview.Preview

/**
 * Standard material clock picker component.
 *
 * @param label Descriptor for what time we are collecting.
 * @param hour Initially selected hour.
 * @param minute Initially selected minute.
 * @param onTimeSelected Callback triggered once the user confirms a choice from the dialog.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeFieldWithPicker(
    label: String,
    hour: Int, minute: Int,
    onTimeSelected: (Int, Int) -> Unit
) {
    var showPicker by remember { mutableStateOf(false) }
    val h = if (hour == 0) 12 else if (hour > 12) hour - 12 else hour
    val amPm = if (hour < 12) "AM" else "PM"
    val displayText = "%d:%02d %s".format(h, minute, amPm)

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.AccessTime, contentDescription = null, tint = Color(0xFF364153), modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(4.dp))
            Text(text = label, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color(0xFF364153))
        }
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = displayText,
            onValueChange = {},
            modifier = Modifier.fillMaxWidth().height(50.dp),
            readOnly = true,
            shape = RoundedCornerShape(8.dp),
            trailingIcon = {
                IconButton(onClick = { showPicker = true }) {
                    Icon(Icons.Default.AccessTime, contentDescription = "Seleccionar hora", tint = Color(0xFF364153))
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF0F2C59),
                unfocusedBorderColor = Color(0xFFD1D5DC)
            ),
            singleLine = true
        )
    }

    if (showPicker) {
        val timePickerState = rememberTimePickerState(initialHour = hour, initialMinute = minute, is24Hour = false)
        Dialog(onDismissRequest = { showPicker = false }) {
            androidx.compose.material3.Surface(shape = RoundedCornerShape(16.dp), color = Color.White) {
                Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(label.replace(" *", ""), fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF0F2C59))
                    Spacer(Modifier.height(16.dp))
                    TimePicker(
                        state = timePickerState,
                        colors = TimePickerDefaults.colors(
                            clockDialColor = Color(0xFFF3F4F6),
                            selectorColor = Color(0xFF0F2C59),
                            containerColor = Color.White,
                            clockDialSelectedContentColor = Color.White,
                            clockDialUnselectedContentColor = Color(0xFF364153),
                            timeSelectorSelectedContainerColor = Color(0xFF0F2C59).copy(alpha = 0.1f),
                            timeSelectorUnselectedContainerColor = Color(0xFFF3F4F6),
                            timeSelectorSelectedContentColor = Color(0xFF0F2C59),
                            timeSelectorUnselectedContentColor = Color(0xFF364153)
                        )
                    )
                    Spacer(Modifier.height(16.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedButton(onClick = { showPicker = false }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(8.dp)) {
                            Text("Cancelar")
                        }
                        Button(
                            onClick = { onTimeSelected(timePickerState.hour, timePickerState.minute); showPicker = false },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F2C59))
                        ) { Text("Aceptar", color = Color.White) }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TimeFieldWithPickerPreview() {
    TimeFieldWithPicker("Hora Salida", 8, 30, { _,_ -> })
}
