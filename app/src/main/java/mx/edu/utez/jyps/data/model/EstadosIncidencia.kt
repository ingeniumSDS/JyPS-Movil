package mx.edu.utez.jyps.data.model

/**
 * Enumeration representing the possible states of an incident or pass request.
 * Ported from Java backend (EstadosIncidencia.java).
 */
enum class EstadosIncidencia {
    PENDIENTE,
    APROBADO,
    RECHAZADO,
    CADUCADO,
    USADO,
    A_TIEMPO,
    RETARDO,
    FUERA
}
