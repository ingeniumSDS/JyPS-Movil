package mx.edu.utez.jyps.data.model

/**
 * Matches DepartamentoResponse: { id, nombre, descripcion, jefeId }
 */
data class Departamento(
    val id: Long = 0,
    val nombre: String = "",
    val descripcion: String = "",
    val jefeId: Long = 0
)
