package mx.edu.utez.jyps.data.model

/**
 * Data class representing a department response from the API.
 */
data class DepartamentoResponse(
    val id: Long,
    val nombre: String,
    val descripcion: String,
    val jefeId: Long?,
    val nombreJefe: String?,
    val activo: Boolean,
    val totalEmpleados: Long
)

/**
 * Request body for CREATING a department.
 */
data class CreateDepartmentRequest(
    val nombre: String,
    val descripcion: String,
    val jefeId: Long = 0L,
    val activo: Boolean = true
)

/**
 * Request body for UPDATING a department.
 */
data class UpdateDepartmentRequest(
    val id: Long,
    val nombre: String,
    val descripcion: String,
    val jefeId: Long = 0L,
    val activo: Boolean
)

/**
 * Request body for changing the active status of a department.
 */
data class ToggleStatusRequest(
    val departamentoId: Long
)
