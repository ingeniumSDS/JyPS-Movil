package mx.edu.utez.jyps.data.model

import java.time.LocalDate
import java.time.LocalTime

/**
 * Domain model for a Pass (Pase de Salida).
 * Based on Spring Boot entity: PaseDeSalidaEntity.java
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
