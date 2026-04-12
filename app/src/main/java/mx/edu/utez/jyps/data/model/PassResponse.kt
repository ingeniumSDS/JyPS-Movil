package mx.edu.utez.jyps.data.model

/**
 * Data Transfer Object for the Personnel Pass response.
 * Maps directly to the backend response for the scan/checkout operation.
 *
 * @property id Unique database identifier.
 * @property empleadoId ID of the employee owning the pass.
 * @property nombreCompleto Full name of the employee.
 * @property horaSolicitada ISO-8601 string of the requested time.
 * @property fechaSolicitud ISO-8601 string of the request date.
 * @property QR Unique 6-character alphanumeric code.
 * @property estado The current state of the pass (matches [EstadosIncidencia]).
 * @property horaSalidaReal ISO-8601 timestamp of when the employee officially checked out.
 * @property horaEsperada ISO-8601 timestamp of the return deadline (null for no-return passes).
 */
data class PassResponse(
    val id: Long,
    val empleadoId: Long,
    val nombreCompleto: String,
    val horaSolicitada: String,
    val fechaSolicitud: String,
    val QR: String,
    val estado: String,
    val horaSalidaReal: String? = null,
    val horaEsperada: String? = null
)
