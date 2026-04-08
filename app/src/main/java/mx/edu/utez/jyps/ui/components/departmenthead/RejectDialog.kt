package mx.edu.utez.jyps.ui.components.departmenthead

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

/**
 * Predefined rejection reasons shown as radio buttons in the reject dialog.
 */
private val PRESET_REASONS = listOf(
    "Información incompleta",
    "Sin evidencia adjunta",
    "Fecha no válida",
    "Motivo no justificado",
    "Duplicado"
)

/**
 * Dialog for confirming rejection of a request with a mandatory reason.
 * The user can either select a preset reason or write a custom one.
 * Selecting a preset clears the custom field and vice versa.
 *
 * @param employeeName Name displayed in the prompt.
 * @param onDismiss Callback to close without rejecting.
 * @param onConfirm Callback with the chosen/written reason string.
 */
@Composable
fun RejectDialog(
    employeeName: String,
    onDismiss: () -> Unit,
    onConfirm: (reason: String) -> Unit
) {
    var selectedPreset by remember { mutableStateOf<String?>(null) }
    var customReason by remember { mutableStateOf("") }

    // Confirm is enabled when at least one source has content
    val isConfirmEnabled = selectedPreset != null || customReason.isNotBlank()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            shape = RoundedCornerShape(16.dp),
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Rechazar Solicitud",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color(0xFF1F2937)
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cerrar",
                            tint = Color(0xFF6B7280)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = buildAnnotatedString {
                        append("Selecciona o escribe el motivo del rechazo para ")
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(employeeName)
                        }
                    },
                    fontSize = 14.sp,
                    color = Color(0xFF374151)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Motivos predefinidos:",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = Color(0xFF1F2937)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Preset radio buttons
                PRESET_REASONS.forEach { reason ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        RadioButton(
                            selected = selectedPreset == reason,
                            onClick = {
                                selectedPreset = reason
                                // Clear custom reason when preset is selected
                                customReason = ""
                            },
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

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "O escribe un motivo personalizado:",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = Color(0xFF1F2937)
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = customReason,
                    onValueChange = {
                        customReason = it
                        // Clear preset when typing a custom reason
                        if (it.isNotBlank()) selectedPreset = null
                    },
                    placeholder = { Text("Describe el motivo del rechazo...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Cancel
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = "Cancelar",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Confirm rejection
                Button(
                    onClick = {
                        val reason = selectedPreset ?: customReason
                        onConfirm(reason)
                    },
                    enabled = isConfirmEnabled,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFDC3545),
                        disabledContainerColor = Color(0xFFDC3545).copy(alpha = 0.4f)
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = "Confirmar Rechazo",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RejectDialogPreview() {
    RejectDialog(
        employeeName = "Juan Pérez García",
        onDismiss = {},
        onConfirm = {}
    )
}
