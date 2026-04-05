package mx.edu.utez.jyps.data.model

import java.time.LocalDate

/**
 * Data class representing a Justification Request.
 * Mapped from backend JustificanteEntity.java.
 *
 * @param id Unique identifier.
 * @param empleadoId Employee ID who requests the justification.
 * @param jefeId Manager ID who approves the justification.
 * @param fechaSolicitada Date for which the justification is requested.
 * @param fechaSolicitud Date when the request was made.
 * @param detalles Detailed explanation.
 * @param archivos List of attached file paths or URLs.
 * @param estado Current status of the request.
 */
data class Justificante(
    val id: Long? = null,
    val empleadoId: Long,
    val jefeId: Long,
    val fechaSolicitada: LocalDate,
    val fechaSolicitud: LocalDate = LocalDate.now(),
    val detalles: String,
    val archivos: List<String> = emptyList(),
    val estado: EstadosIncidencia = EstadosIncidencia.PENDIENTE
)
