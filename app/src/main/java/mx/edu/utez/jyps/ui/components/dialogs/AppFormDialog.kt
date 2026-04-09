package mx.edu.utez.jyps.ui.components.dialogs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

/**
 * A reusable container for application forms presented as Dialogs.
 * Evaluates the Slot API UX pattern to decouple form visuals from domain logic.
 *
 * @param title Bold header text spanning the dialog top bar.
 * @param onDismissRequest Called when user clicks outside or on the 'X'.
 * @param onCancel Called when the explicit Cancel button is pressed.
 * @param onSave Called when the primary action button is pressed.
 * @param isSaveEnabled Restricts the primary action button interaction.
 * @param isLoading Renders a loading spinner replacing the title on the Save button.
 * @param content The composable elements slot matching the business specific fields.
 */
@Composable
fun AppFormDialog(
    title: String,
    onDismissRequest: () -> Unit,
    onCancel: () -> Unit,
    onSave: () -> Unit,
    isSaveEnabled: Boolean = true,
    isLoading: Boolean = false,
    content: @Composable () -> Unit
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .wrapContentHeight(),
            shape = RoundedCornerShape(12.dp),
            color = Color.White
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color(0xFF0D2A4C) // Main typography deep blue
                    )
                    IconButton(onClick = onDismissRequest) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = Color(0xFF6A7282))
                    }
                }
                
                HorizontalDivider(color = Color(0xFFE5E7EB))
                
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    content()
                }
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onCancel,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, Color(0xFF0D2A4C)),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF0D2A4C))
                    ) {
                        Text("Cancelar", fontWeight = FontWeight.Medium)
                    }
                    
                    Button(
                        onClick = onSave,
                        enabled = isSaveEnabled && !isLoading,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF28A745),
                            disabledContainerColor = Color(0xFF28A745).copy(alpha = 0.5f)
                        )
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                        } else {
                            Text("Guardar Cambios", fontWeight = FontWeight.Medium, color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppFormDialogPreview() {
    AppFormDialog(
        title = "Preview Dialog",
        onDismissRequest = {},
        onCancel = {},
        onSave = {},
        content = {
            Text("Inner content mock")
        }
    )
}
