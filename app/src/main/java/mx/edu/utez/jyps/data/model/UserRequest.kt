package mx.edu.utez.jyps.data.model

/**
 * DTO for creating/updating a user.
 * Matches the backend's RegistrarUsuarioCommand.
 * Uses Strings for time fields formatted as 'HH:mm:ss' to conform to Jackson's LocalTime expectation.
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
