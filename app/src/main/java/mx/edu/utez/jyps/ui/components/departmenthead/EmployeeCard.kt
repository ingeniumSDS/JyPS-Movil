package mx.edu.utez.jyps.ui.components.departmenthead

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mx.edu.utez.jyps.data.model.EmployeeItem
import androidx.compose.ui.graphics.SolidColor

/**
 * Redesigned EmployeeCard following Figma mockup strictly.
 * 
 * @param employee The employee data.
 * @param onEditClick Callback for the main edit action.
 */
@Composable
fun EmployeeCard(
    employee: EmployeeItem,
    onEditClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Circular Avatar
                Surface(
                    modifier = Modifier.size(40.dp),
                    shape = CircleShape,
                    color = Color(0xFF0F2C59) // Dark Blue
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = employee.fullName.firstOrNull()?.toString() ?: "",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }
                }

                // Name and Badges
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = employee.fullName,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F2C59)
                    )
                    
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        // Role Badge (Standard: Empleado)
                        EmployeeBadge(
                            text = "Empleado", 
                            containerColor = Color(0xFFE7F1FF), // Light Blue
                            contentColor = Color(0xFF007BFF)    // Strong Blue
                        )
                        
                        // Department Badge
                        EmployeeBadge(
                            text = employee.department,
                            icon = Icons.Default.Business,
                            containerColor = Color(0xFFF1F3F5),
                            contentColor = Color(0xFF6A7282)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Contact Info
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ContactInfoRow(icon = Icons.Default.Email, text = employee.email)
                ContactInfoRow(icon = Icons.Default.Phone, text = employee.phone)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Wide Action Button
            OutlinedButton(
                onClick = onEditClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                border = ButtonDefaults.outlinedButtonBorder.copy(brush = SolidColor(Color(0xFF0F2C59))),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF0F2C59))
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Editar Información",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}



@Preview
@Composable
private fun EmployeeCardPreview() {
    EmployeeCard(
        employee = EmployeeItem(
            fullName = "Juan Pérez García",
            employeeId = "EMP001",
            position = "Docente",
            department = "Tecnologías de la Informac...",
            email = "juan.perez@utez.edu.mx",
            phone = "777 123 4567",
            id = 1,
            isActive = true
        )
    )
}
