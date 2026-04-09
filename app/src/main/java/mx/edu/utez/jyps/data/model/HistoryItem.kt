package mx.edu.utez.jyps.data.model

import mx.edu.utez.jyps.data.model.EstadosIncidencia

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
 * @property fileName Attached evidence path/name.
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
    val fileName: String? = null,
    val rejectionReason: String? = null,
    val internalInfo: String? = null
)