package mx.edu.utez.jyps.ui.components.dialogs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Attachment
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import mx.edu.utez.jyps.data.model.HistoryItem
import mx.edu.utez.jyps.data.model.EstadosIncidencia
import android.net.Uri

/**
 * Dialog displaying detailed information about a submitted justification.
 *
 * @param item The justification history record to show.
 * @param onDismissRequest Callback to hide the dialog.
 */
@Composable
fun JustificationDetailDialog(
    item: HistoryItem,
    onDismissRequest: () -> Unit,
    onDownload: (String) -> Unit = {},
    localFileUri: Uri? = null,
    isDownloading: Boolean = false
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Cerrar")
            }
        },
        title = {
            Text(
                text = "Detalles del Justificante",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                StatusBadge(status = item.status)

                DetailRow(
                    icon = Icons.Default.CalendarMonth,
                    label = "Fecha Solicitada",
                    value = item.date
                )

                DetailRow(
                    icon = Icons.Default.Description,
                    label = "Motivo del Justificante",
                    value = item.description
                )

                item.fileName?.let { fileName ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        DetailRow(
                            icon = Icons.Default.Attachment,
                            label = "Evidencia Adjunta (Toca para descargar)",
                            value = item.displayFileName ?: fileName,
                            valueColor = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .weight(1f)
                                .clickable(enabled = !isDownloading) { onDownload(fileName) }
                        )

                        if (isDownloading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                item.rejectionReason?.let {
                    DetailRow(
                        icon = Icons.Default.Info,
                        label = "Comentarios del Jefe",
                        value = it,
                        valueColor = Color.Red
                    )
                }

                // File Preview (Only show if downloaded)
                localFileUri?.let { uri ->
                    Column(modifier = Modifier.padding(top = 8.dp)) {
                        Text(
                            text = "Previsualización:",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                                .background(Color(0xFFF9FAFB)),
                            contentAlignment = Alignment.Center
                        ) {
                            if (item.fileName?.endsWith(".pdf", true) == true) {
                                Icon(
                                    imageVector = Icons.Default.PictureAsPdf,
                                    contentDescription = null,
                                    modifier = Modifier.size(48.dp),
                                    tint = Color(0xFFC62828)
                                )
                            } else {
                                AsyncImage(
                                    model = uri,
                                    contentDescription = "Preview",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    }
                }
            }
        },
        shape = RoundedCornerShape(16.dp)
    )
}

/**
 * Internal UI component to render a labeled data row with an icon.
 */
@Composable
private fun DetailRow(
    icon: ImageVector,
    label: String,
    value: String,
    valueColor: Color = Color.Unspecified,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = label,
                fontSize = 12.sp,
                color = Color.Gray,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = value,
                fontSize = 15.sp,
                fontWeight = FontWeight.Normal,
                color = valueColor
            )
        }
    }
}

/**
 * Status indicator badge with contextual colors.
 */
@Composable
private fun StatusBadge(status: EstadosIncidencia) {
    val (backgroundColor, textColor) = when (status) {
        EstadosIncidencia.APROBADO -> Color(0xFFE8F5E9) to Color(0xFF2E7D32)
        EstadosIncidencia.RECHAZADO -> Color(0xFFFFEBEE) to Color(0xFFC62828)
        else -> Color(0xFFFFF3E0) to Color(0xFFEF6C00)
    }

    Surface(
        color = backgroundColor,
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = status.name,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}

@Preview(showBackground = true)
@Composable
fun JustificationDetailDialogPreview() {
    JustificationDetailDialog(
        item = HistoryItem(
            id = "1",
            type = "Justificante",
            status = EstadosIncidencia.PENDIENTE,
            description = "Consulta médica por gripe estacional y malestar general.",
            date = "12/04/2026",
            time = "N/A",
            code = "JUST-100",
            fileName = "receta_medica.pdf"
        ),
        onDismissRequest = {}
    )
}
