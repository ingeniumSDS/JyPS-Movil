package mx.edu.utez.jyps.data.model

/**
 * Data Transfer Object for the Personnel Pass response.
 * Maps directly to 'PaseDeSalidaEntity' from the backend.
 *
 * @property id Unique database identifier.
 * @property empleadoId ID of the employee owning the pass.
 * @property nombreCompleto Full name of the employee (Transitory/Flattened).
 * @property jefeId ID of the department head who authorized/will authorize.
 * @property horaSolicitada ISO-8601 string of the requested departure time.
 * @property fechaSolicitud ISO-8601 string of the request date (YYYY-MM-DD).
 * @property horaSalidaReal ISO-8601 timestamp of official exit.
 * @property horaEsperada ISO-8601 timestamp of the return deadline.
 * @property detalles Textual justification for the pass.
 * @property estado The current state of the pass.
 * @property QR Unique alphanumeric code for the pass.
 * @property archivos List of associated evidence files (Optional/Ignored for passes).
 * @property comentario Manager feedback or rejection reason.
 * @property horaRetornoReal ISO-8601 timestamp of official return.
 */
data class PassResponse(
    val id: Long,
    val empleadoId: Long,
    val nombreCompleto: String? = null,
    val jefeId: Long? = null,
    val horaSolicitada: String,
    val fechaSolicitud: String,
    val horaSalidaReal: String? = null,
    val horaEsperada: String,
    val descripcion: String? = null,
    val estado: String,
    val QR: String? = null,
    val archivos: List<String>? = null,
    val comentario: String? = null,
    val horaRetornoReal: String? = null
)

