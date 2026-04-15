package mx.edu.utez.jyps.data.model

/**
 * Data class representing an employee managed by a Department Head.
 * 
 * @property id Unique identifier (primary key).
 * @property fullName Complete name of the employee.
 * @property email Institutional email address.
 * @property phone Contact phone number.
 * @property employeeId Institutional ID string.
 * @property position Job title or role.
 * @property department Assigned department name.
 * @property isActive Boolean status indicating if the employee is currently active in the system.
 */
data class EmployeeItem(
    val id: Long = 0,
    val fullName: String,
    val email: String,
    val phone: String,
    val employeeId: String,
    val position: String,
    val department: String,
    val isActive: Boolean = true
)
