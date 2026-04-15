package mx.edu.utez.jyps.ui.components.departmenthead

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import mx.edu.utez.jyps.data.model.RequestItem
import mx.edu.utez.jyps.data.model.RequestStatus
import mx.edu.utez.jyps.data.model.RequestType
import androidx.compose.ui.graphics.SolidColor

/**
 * Full-screen dialog showing the details of a pending **exit pass** request.
 * Contains employee info, date/time details, reason, and Approve/Reject buttons.
 * Approve/Reject buttons are only shown when the pass is still [RequestStatus.PENDING].
 *
 * @param item The request data to display.
 * @param onDismiss Callback to close the dialog.
 * @param onApprove Callback when the head approves the request.
 * @param onReject Callback when the head wants to reject (opens RejectDialog).
 */
@Composable
fun PassDetailDialog(
    item: RequestItem,
    onDismiss: () -> Unit,
    onApprove: () -> Unit = {},
    onReject: () -> Unit = {}
) {
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
                        text = "Detalle de Pase de Salida",
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

                Spacer(modifier = Modifier.height(12.dp))

                // Status badge
                val (bgColor, textColor, label) = statusConfig(item.status)
                Text(
                    text = label,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = textColor,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = bgColor, shape = RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Employee info section
                SectionHeader(
                    icon = Icons.Default.Person,
                    title = "Información del Empleado"
                )
                Text(
                    text = "Nombre: ${item.employeeName}",
                    fontSize = 14.sp,
                    color = Color(0xFF374151),
                    modifier = Modifier.padding(top = 4.dp)
                )
                Text(
                    text = "Email: ${item.employeeEmail}",
                    fontSize = 14.sp,
                    color = Color(0xFF374151),
                    modifier = Modifier.padding(top = 2.dp)
                )

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 12.dp),
                    color = Color(0xFFF3F4F6)
                )

                // Details section
                SectionHeader(
                    icon = Icons.Default.CalendarMonth,
                    title = "Detalles"
                )
                Text(
                    text = "Fecha: ${item.date}",
                    fontSize = 14.sp,
                    color = Color(0xFF374151),
                    modifier = Modifier.padding(top = 4.dp)
                )
                if (item.exitTime != null) {
                    Text(
                        text = "Hora de salida: ${item.exitTime}",
                        fontSize = 14.sp,
                        color = Color(0xFF374151),
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
                Text(
                    text = "Motivo:",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF374151),
                    modifier = Modifier.padding(top = 8.dp)
                )
                Text(
                    text = item.reason,
                    fontSize = 14.sp,
                    color = Color(0xFF374151),
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF9FAFB), RoundedCornerShape(8.dp))
                        .padding(12.dp)
                )

                // Action buttons — only for pending requests
                if (item.status == RequestStatus.PENDING) {
                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = onApprove,
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF28A745)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = Color.White)
                        Text(
                            text = "Aprobar",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = onReject,
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(10.dp),
                        border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(
                            brush = SolidColor(Color(0xFFDC3545))
                        )
                    ) {
                        Icon(Icons.Default.Close, contentDescription = null, tint = Color(0xFFDC3545))
                        Text(
                            text = "Rechazar",
                            color = Color(0xFFDC3545),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Returns (backgroundColor, textColor, label) for a given status.
 *
 * @param status The input target state evaluating the condition.
 * @return Defined tuple properties for rendering the badge state.
 */
private fun statusConfig(status: RequestStatus): Triple<Color, Color, String> = when (status) {
    RequestStatus.PENDING -> Triple(Color(0xFFFEF9C2), Color(0xFF894B00), "Pendiente")
    RequestStatus.APPROVED -> Triple(Color(0xFFDCFCE7), Color(0xFF016630), "Aprobado")
    RequestStatus.REJECTED -> Triple(Color(0xFFFFE2E2), Color(0xFF9F0712), "Rechazado")
    RequestStatus.USED -> Triple(Color(0xFFF3F4F6), Color(0xFF1E2939), "Usado")
}



@Preview(showBackground = true)
@Composable
fun PassDetailDialogPreview() {
    PassDetailDialog(
        item = RequestItem(
            id = "1",
            numericId = 1L,
            employeeName = "Juan Pérez García",
            employeeEmail = "juan.perez@utez.edu.mx",
            requestType = RequestType.PASS,
            reason = "Cita con dentista - Limpieza dental programada",
            date = "28 de febrero de 2026",
            time = "08:30 a.m.",
            exitTime = "10:00",
            status = RequestStatus.PENDING
        ),
        onDismiss = {}
    )
}
