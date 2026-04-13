package mx.edu.utez.jyps.data.model

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object representing an attachment within a Justification.
 *
 * @property originalName The name of the file as uploaded by the user.
 * @property downloadUrl The backend URL to retrieve the file content.
 */
data class JustificationFileResponse(
    @SerializedName("nombreOriginal")
    val originalName: String,
    @SerializedName("urlDescarga")
    val downloadUrl: String
)

/**
 * Data Transfer Object representing the server response for a Justification request.
 * Matches backend Justificante schema.
 *
 * @property id Unique database identifier for the justification.
 * @property employeeId Identifier of the employee who made the request.
 * @property fullName Display name of the employee.
 * @property managerId Identifier of the manager responsible for approval.
 * @property requestedDate The specific date intended to be justified (YYYY-MM-DD).
 * @property applicationDate The date when the request was officially submitted (YYYY-MM-DD).
 * @property description The textual explanation provided by the employee.
 * @property status Current workflow state (e.g., PENDING, APPROVED, REJECTED).
 * @property managerComment Feedback provided by the manager during approval/rejection.
 * @property attachments List of files linked as evidence for the justification.
 */
data class JustificationResponse(
    val id: Long,
    @SerializedName("empleadoId")
    val employeeId: Long,
    val nombreCompleto: String,
    @SerializedName("jefeId")
    val managerId: Long,
    @SerializedName("fechaSolicitada")
    val requestedDate: String,
    @SerializedName("fechaSolicitud")
    val applicationDate: String,
    @SerializedName("descripcion")
    val description: String,
    @SerializedName("estado")
    val status: String,
    @SerializedName("comentario")
    val managerComment: String?,
    @SerializedName("archivos")
    val attachments: List<JustificationFileResponse> = emptyList()
)

/**
 * Data Transfer Object for reviewing a justification.
 *
 * @property justificanteId The ID of the justification to review.
 * @property estado The new state (e.g., APROBADO, RECHAZADO).
 * @property comentario Manager's observation.
 */
data class ReviewJustificationRequest(
    val justificanteId: Long,
    val estado: String,
    val comentario: String?
)
