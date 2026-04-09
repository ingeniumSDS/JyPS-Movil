package mx.edu.utez.jyps.ui.components.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mx.edu.utez.jyps.data.model.Departamento
import mx.edu.utez.jyps.ui.theme.JyPSTheme

/**
 * A detailed card representing a department with action buttons.
 *
 * @param departamento Data model containing name, description, and status.
 * @param onEdit Click handler for the edit action.
 * @param onToggleStatus Click handler for activating/deactivating the department.
 */
@Composable
fun DepartmentCard(
    departamento: Departamento,
    onEdit: () -> Unit,
    onToggleStatus: () -> Unit
) {
    val statusColor = if (departamento.estaActivo) Color(0xFF0F2C59) else Color(0xFF99A1AF)
    val statusLabelColor = if (departamento.estaActivo) Color(0xFF0F2C59) else Color(0xFF6A7282)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Icon Container
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(statusColor, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Business,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = departamento.nombre,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = statusLabelColor,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        if (!departamento.estaActivo) {
                            InactiveBadge()
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = departamento.descripcion,
                        fontSize = 12.sp,
                        color = statusLabelColor,
                        lineHeight = 16.sp,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = Color.Black.copy(alpha = 0.05f))
            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onEdit,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    border = borderWithColor(Color(0xFF0F2C59)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF0F2C59))
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.size(8.dp))
                    Text("Editar")
                }

                val actionText = if (departamento.estaActivo) "Desactivar" else "Activar"
                val actionColor = if (departamento.estaActivo) Color(0xFFDC3545) else Color(0xFF28A745)
                val actionIcon = if (departamento.estaActivo) Icons.Default.Cancel else Icons.Default.CheckCircle

                OutlinedButton(
                    onClick = onToggleStatus,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    border = borderWithColor(actionColor),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = actionColor)
                ) {
                    Icon(actionIcon, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(actionText)
                }
            }
        }
    }
}

@Composable
private fun InactiveBadge() {
    Box(
        modifier = Modifier
            .background(Color(0xFFFFE2E2), RoundedCornerShape(100.dp))
            .padding(horizontal = 8.dp, vertical = 2.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Icon(
                imageVector = Icons.Default.Cancel,
                contentDescription = null,
                tint = Color(0xFFC10007),
                modifier = Modifier.size(12.dp)
            )
            Text(
                text = "Inactivo",
                color = Color(0xFFC10007),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun borderWithColor(color: Color) = androidx.compose.foundation.BorderStroke(1.7.dp, color)

@Preview(showBackground = true)
@Composable
fun DepartmentCardPreview() {
    JyPSTheme {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            DepartmentCard(
                departamento = Departamento(1, "TI", "Infraestructura tecnológica.", 1, true),
                onEdit = {},
                onToggleStatus = {}
            )
            DepartmentCard(
                departamento = Departamento(2, "Biblioteca", "Gestión de acervo.", 1, false),
                onEdit = {},
                onToggleStatus = {}
            )
        }
    }
}
