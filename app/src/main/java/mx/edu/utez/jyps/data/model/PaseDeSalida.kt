package mx.edu.utez.jyps.data.model

import java.time.LocalDate
import java.time.LocalTime

/**
 * Domain model for a Pass (Pase de Salida).
 * Based on Spring Boot entity: PaseDeSalidaEntity.java.
 *
 * @property id Unique identifier. Null if it's a new request.
 * @property empleado The user requesting the pass.
 * @property jefe The manager/head approving the pass.
 * @property horaSolicitada The requested exit time.
 * @property fechaSolicitud The date the request was made.
 * @property detalles Description or rationale for the exit.
 * @property estado The current lifecycle status of the pass.
 */
data class PaseDeSalida(
    val id: Long? = null,
    val empleado: Usuario,
    val jefe: Usuario,
    val horaSolicitada: LocalTime,
    val fechaSolicitud: LocalDate,
    val detalles: String,
    val estado: EstadosIncidencia
)
