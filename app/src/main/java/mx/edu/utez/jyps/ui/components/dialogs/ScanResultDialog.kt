package mx.edu.utez.jyps.ui.components.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mx.edu.utez.jyps.viewmodel.ScannedPassInfo
import mx.edu.utez.jyps.viewmodel.ScannerStatus

/**
 * Parametric Material 3 Dialog that covers all 5 scanner outcome states.
 * The visual configuration (icon, color, title, body) adapts automatically
 * to the sealed [ScannerStatus] subclass passed in.
 *
 * @param status The current scanner status — must not be [ScannerStatus.Idle].
 * @param onDismiss Callback to reset the scanner and close this dialog.
 */
@Composable
fun ScanResultDialog(
    status: ScannerStatus,
    onDismiss: () -> Unit
) {
    val config = scanDialogConfig(status) ?: return

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        shape = RoundedCornerShape(16.dp),
        title = null,
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Status Header
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(config.headerBg, RoundedCornerShape(12.dp))
                        .padding(vertical = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = config.icon,
                        contentDescription = null,
                        tint = config.accentColor,
                        modifier = Modifier.size(40.dp)
                    )
                    Text(
                        text = config.title,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = config.accentColor,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = config.subtitle,
                        fontSize = 14.sp,
                        color = Color(0xFF4A5565),
                        textAlign = TextAlign.Center
                    )
                }

                // Pass holder info (only for valid codes)
                config.passInfo?.let { info ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = info.name,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F2C59),
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = info.email,
                            fontSize = 13.sp,
                            color = Color(0xFF64748B)
                        )
                    }

                    Divider(color = Color(0xFFE2E8F0))

                    // Time details grid
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF8FAFC), RoundedCornerShape(8.dp))
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ScanDetailRow(label = "Fecha", value = info.date)
                        ScanDetailRow(label = "Hora de salida", value = info.exitTime)
                        ScanDetailRow(label = "Límite de regreso", value = info.returnDeadline)
                        if (info.actualReturnTime.isNotEmpty()) {
                            ScanDetailRow(
                                label = "Hora de regreso",
                                value = info.actualReturnTime,
                                valueColor = config.accentColor
                            )
                        }
                        ScanDetailRow(label = "Código", value = info.code, isMono = true)
                    }
                }

                // Error message for invalid codes (no passInfo)
                if (config.passInfo == null) {
                    Text(
                        text = config.bodyMessage,
                        fontSize = 14.sp,
                        color = Color(0xFF4A5565),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = config.accentColor)
            ) {
                Text("Cerrar", color = Color.White, fontWeight = FontWeight.Medium)
            }
        }
    )
}

/**
 * Maps each [ScannerStatus] subclass to display configuration for the dialog.
 * Returns null for [ScannerStatus.Idle] since no dialog should be shown.
 *
 * @property icon The header symbol defining the mapping sequence.
 * @property accentColor Global tint overlay constraint property maps.
 * @property headerBg Surface bounds limits mappings definition mappings.
 * @property title Header target.
 * @property subtitle Property sequences bounding contexts definitions.
 * @property bodyMessage Additional explicitly textual context definition mapping contexts.
 * @property passInfo Resolved tuple boolean mapping object context explicitly mapping bound expressions parameters explicit definitions limits logic boundary.
 */
private data class ScanDialogConfig(
    val icon: ImageVector,
    val accentColor: Color,
    val headerBg: Color,
    val title: String,
    val subtitle: String,
    val bodyMessage: String = "",
    val passInfo: ScannedPassInfo? = null
)

/**
 * Explicit boundary definition arrays string tuples map property bounds.
 *
 * @param status Target bound sequences contexts limits explicit limits contexts bounding definition constraint values explicitly constraint contexts explicitly property bounds maps binding mappings explicitly definitions logic map bindings explicitly explicitly bounded parameter definitions variable constraint mapping boolean arrays limits bounds values explicitly context property mapping contexts explicitly maps limits explicit contexts mappings mappings expressions explicit bounding maps.
 * @return Context constraint mapping property bounding expressions definitions limits bounded explicitly variables definitions implicitly mapped definitions strings explicit boundary tuples constraint explicitly.
 */
private fun scanDialogConfig(status: ScannerStatus): ScanDialogConfig? = when (status) {
    is ScannerStatus.Idle -> null

    is ScannerStatus.ExitGranted -> ScanDialogConfig(
        icon = Icons.Default.CheckCircle,
        accentColor = Color(0xFF28A745),
        headerBg = Color(0xFFF0FDF4),
        title = "✅ Salida Autorizada",
        subtitle = "Regreso requerido antes de las ${status.info.returnDeadline}",
        passInfo = status.info
    )

    is ScannerStatus.ExitNoReturn -> ScanDialogConfig(
        icon = Icons.Default.CheckCircle,
        accentColor = Color(0xFF0D6EFD),
        headerBg = Color(0xFFEFF6FF),
        title = "✅ Salida Autorizada",
        subtitle = "No se requiere regreso — Fin de jornada",
        passInfo = status.info
    )

    is ScannerStatus.ReturnOnTime -> ScanDialogConfig(
        icon = Icons.Default.AccessTime,
        accentColor = Color(0xFF28A745),
        headerBg = Color(0xFFF0FDF4),
        title = "✅ Regreso Verificado",
        subtitle = "Regresó dentro del tiempo permitido",
        passInfo = status.info
    )

    is ScannerStatus.ReturnLate -> ScanDialogConfig(
        icon = Icons.Default.Warning,
        accentColor = Color(0xFFF59E0B),
        headerBg = Color(0xFFFFFBEB),
        title = "⚠️ Regreso con Retraso",
        subtitle = "El empleado excedió el tiempo límite de regreso",
        passInfo = status.info
    )

    is ScannerStatus.ExpiredPass -> ScanDialogConfig(
        icon = Icons.Default.AccessTime,
        accentColor = Color(0xFF6B7280),
        headerBg = Color(0xFFF3F4F6),
        title = "🕐 Pase Caducado",
        subtitle = "Este pase no fue utilizado y ya expiró",
        bodyMessage = "El pase de salida aprobado no fue usado durante la jornada o el día en que fue solicitado. Ya no puede ser utilizado.",
        passInfo = status.info
    )

    is ScannerStatus.AlreadyUsed -> ScanDialogConfig(
        icon = Icons.Default.Lock,
        accentColor = Color(0xFF6366F1),
        headerBg = Color(0xFFEEF2FF),
        title = "🔒 Pase Ya Utilizado",
        subtitle = "Este pase ya completó su ciclo de uso",
        bodyMessage = "El pase ya fue usado en su totalidad y no puede volver a escanearse.",
        passInfo = status.info
    )

    is ScannerStatus.InvalidCode -> ScanDialogConfig(
        icon = Icons.Default.Error,
        accentColor = Color(0xFFDC3545),
        headerBg = Color(0xFFFFF5F5),
        title = "❌ Código No Reconocido",
        subtitle = "Este código no está registrado en el sistema",
        bodyMessage = status.reason
    )
}

// ─── Previews ────────────────────────────────────────────────────────────────

private val previewPassInfo = ScannedPassInfo(
    name = "Juan Pérez García",
    email = "juan.perez@utez.edu.mx",
    code = "PASE004",
    date = "lunes, 6 de abril de 2026",
    exitTime = "09:30 a.m.",
    returnDeadline = "12:30 p.m."
)

@Preview(showBackground = true)
@Composable
fun ScanResultDialogExitGrantedPreview() {
    ScanResultDialog(
        status = ScannerStatus.ExitGranted(previewPassInfo),
        onDismiss = {}
    )
}

@Preview(showBackground = true)
@Composable
fun ScanResultDialogReturnLatePreview() {
    ScanResultDialog(
        status = ScannerStatus.ReturnLate(previewPassInfo.copy(actualReturnTime = "01:15 p.m.")),
        onDismiss = {}
    )
}

@Preview(showBackground = true)
@Composable
fun ScanResultDialogInvalidPreview() {
    ScanResultDialog(
        status = ScannerStatus.InvalidCode("Código \"XYZ123\" no encontrado."),
        onDismiss = {}
    )
}

@Preview(showBackground = true)
@Composable
fun ScanResultDialogExpiredPreview() {
    ScanResultDialog(
        status = ScannerStatus.ExpiredPass(previewPassInfo),
        onDismiss = {}
    )
}

@Preview(showBackground = true)
@Composable
fun ScanResultDialogAlreadyUsedPreview() {
    ScanResultDialog(
        status = ScannerStatus.AlreadyUsed(previewPassInfo),
        onDismiss = {}
    )
}
