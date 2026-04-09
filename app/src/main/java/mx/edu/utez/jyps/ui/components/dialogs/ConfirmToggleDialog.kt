package mx.edu.utez.jyps.ui.components.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
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
import mx.edu.utez.jyps.ui.components.admin.DialogHeader
import mx.edu.utez.jyps.ui.theme.JyPSTheme

/**
 * Standard confirmation dialog for activating or deactivating a department.
 *
 * @param isActivating Whether the department is being enabled (true) or disabled (false).
 * @param onClose Callback to dismiss the dialog.
 * @param onConfirm Callback when user confirms the action.
 */
@Composable
fun ConfirmToggleDialog(
    isActivating: Boolean,
    onClose: () -> Unit,
    onConfirm: () -> Unit
) {
    val title = if (isActivating) "Confirmar Activación" else "Confirmar Desactivación"
    val actionText = if (isActivating) "activa" else "desactivas"
    
    Dialog(onDismissRequest = onClose) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color.White,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                DialogHeader(title = title, onClose = onClose)

                Column(modifier = Modifier.padding(24.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .background(Color(0xFFFEF9E7), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = Color(0xFFE67E22),
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        
                        Text(
                            text = "¿Estás seguro de que deseas $actionText este departamento?",
                            fontSize = 16.sp,
                            color = Color(0xFF4A5565),
                            fontWeight = FontWeight.Medium,
                            lineHeight = 22.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = onClose,
                            modifier = Modifier.weight(1f).height(48.dp),
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.7.dp, Color(0xFF0F2C59)),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF0F2C59))
                        ) {
                            Text("Cancelar", fontWeight = FontWeight.SemiBold)
                        }
                        
                        Button(
                            onClick = onConfirm,
                            modifier = Modifier.weight(1f).height(48.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isActivating) Color(0xFF28A745) else Color(0xFF0F2C59)
                            )
                        ) {
                            Text("Confirmar", fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ConfirmToggleDialogPreview() {
    JyPSTheme {
        ConfirmToggleDialog(isActivating = false, onClose = {}, onConfirm = {})
    }
}
