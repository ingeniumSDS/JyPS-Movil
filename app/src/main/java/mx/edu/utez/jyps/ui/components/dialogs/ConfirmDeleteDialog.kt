package mx.edu.utez.jyps.ui.components.dialogs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Standard confirmation dialog for destructive actions (Delete request).
 * Matches Material 3 Figma spec for simple alert modal.
 *
 * @param onConfirm Callback executed when user accepts deletion.
 * @param onCancel Callback executed when user dismisses validation.
 */
@Composable
fun ConfirmDeleteDialog(
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCancel,
        shape = RoundedCornerShape(12.dp),
        containerColor = Color.White,
        title = null,
        text = {
            Text(
                "¿Estás seguro de que deseas eliminar esta solicitud?",
                fontSize = 16.sp,
                color = Color(0xFF364153),
                modifier = Modifier.padding(top = 8.dp)
            )
        },
        confirmButton = {
            OutlinedButton(
                onClick = onConfirm,
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, Color(0xFF0D6EFD)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF0D6EFD))
            ) {
                Text("Aceptar", fontWeight = FontWeight.Medium)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onCancel,
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, Color(0xFF6C757D)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF6C757D))
            ) {
                Text("Cancelar", fontWeight = FontWeight.Medium)
            }
        }
    )
}
