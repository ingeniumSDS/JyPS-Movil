package mx.edu.utez.jyps.data.model

import mx.edu.utez.jyps.data.model.EstadosIncidencia

/**
 * Data class representing a file attached to a history record.
 * 
 * @property technicalName UUID-prefixed name used for backend requests.
 * @property displayName Human-readable original filename.
 */
data class AttachmentItem(
    val technicalName: String,
    val displayName: String
)

/**
 * Data class encapsulating the properties of a Pass or Justification history log record.
 *
 * @property id Unique identifier.
 * @property type Log type (e.g. Justificante, Pase).
 * @property status Resolved state of the record.
 * @property description Full detail text provided.
 * @property date String formatted date.
 * @property time String formatted time.
 * @property code Scannable code validation.
 * @property attachments List of files (1-3) associated with the record.
 * @property rejectionReason Motive of rejection if status is RECHAZADO.
 * @property internalInfo Private internal text information.
 */
data class HistoryItem(
    val id: String,
    val type: String,
    val status: EstadosIncidencia,
    val description: String,
    val date: String,
    val time: String,
    val code: String,
    val attachments: List<AttachmentItem> = emptyList(),
    val rejectionReason: String? = null,
    val internalInfo: String? = null
)