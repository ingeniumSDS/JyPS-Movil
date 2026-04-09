package mx.edu.utez.jyps.ui.components.departmenthead

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mx.edu.utez.jyps.data.model.RequestStatus

/**
 * Coloured pill badge representing the request's lifecycle status.
 *
 * @param status Evaluating parameter bounding the render tuple definitions.
 */
@Composable
fun RequestStatusBadge(status: RequestStatus) {
    val (bgColor, textColor, label) = when (status) {
        RequestStatus.PENDING -> Triple(Color(0xFFFEF9C2), Color(0xFF894B00), "Pendiente")
        RequestStatus.APPROVED -> Triple(Color(0xFFDCFCE7), Color(0xFF016630), "Aprobado")
        RequestStatus.REJECTED -> Triple(Color(0xFFFFE2E2), Color(0xFF9F0712), "Rechazado")
        RequestStatus.USED -> Triple(Color(0xFFF3F4F6), Color(0xFF1E2939), "Usado")
    }

    Text(
        text = label,
        fontSize = 13.sp,
        fontWeight = FontWeight.Medium,
        color = textColor,
        modifier = Modifier
            .background(color = bgColor, shape = RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    )
}


@Preview(showBackground = true)
@Composable
fun RequestStatusBadgePreview() {
    RequestStatusBadge(status = RequestStatus.PENDING)
}
