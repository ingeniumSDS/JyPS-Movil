package mx.edu.utez.jyps.data.model

/**
 * Matches DepartamentoResponse JSON structure.
 *
 * @property id The unique identifier of the department.
 * @property nombre The official name of the department.
 * @property descripcion A brief description of the department's function.
 * @property jefeId The unique identifier of the user who manages this department.
 */
data class Departamento(
    val id: Long = 0,
    val nombre: String = "",
    val descripcion: String = "",
    val jefeId: Long = 0
)
