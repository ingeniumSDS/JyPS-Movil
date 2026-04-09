package mx.edu.utez.jyps.data.model

/**
 * DTO for creating/updating a user.
 * Matches the backend's RegistrarUsuarioCommand.
 * Uses Strings for time fields formatted as 'HH:mm:ss' to conform to Jackson's LocalTime expectation.
 *
 * @property nombre Employee's first name(s).
 * @property apellidoPaterno Employee's paternal surname.
 * @property apellidoMaterno Employee's maternal surname.
 * @property correo Institutional email address.
 * @property telefono Contact phone number.
 * @property horaEntrada Assigned clock-in time.
 * @property horaSalida Assigned clock-out time.
 * @property roles Roles assigned to the user (e.g. "EMPLEADO", "ADMINISTRADOR").
 * @property departamentoId The identifier of the department the employee is assigned to.
 */
data class UserRequest(
    val nombre: String,
    val apellidoPaterno: String,
    val apellidoMaterno: String,
    val correo: String,
    val telefono: String,
    val horaEntrada: String,
    val horaSalida: String,
    val roles: List<String>,
    val departamentoId: Long
)
