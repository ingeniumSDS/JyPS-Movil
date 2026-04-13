package mx.edu.utez.jyps.data.model

/**
 * Data structure for requesting a new exit pass.
 * Maps to the requirements for 'PaseDeSalidaEntity' creation.
 * 
 * @property empleadoId The employee creating the request.
 * @property horaSolicitada Scheduled departure time (ISO-8601).
 * @property fechaSolicitud Scheduled departure date (ISO-8601, YYYY-MM-DD).
 * @property detalles Reason for the exit request.
 */
data class PassRequest(
    val empleadoId: Long,
    val horaSolicitada: String,
    val fechaSolicitud: String,
    val detalles: String
)
