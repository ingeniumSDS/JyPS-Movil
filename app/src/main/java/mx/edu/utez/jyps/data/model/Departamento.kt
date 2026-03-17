package mx.edu.utez.jyps.data.model

data class Departamento(
    val id: Int,
    val nombre: String,
    val idJefe: Int?,
    val descripcion: String,
    val activo: Boolean
)
