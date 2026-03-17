package mx.edu.utez.jyps.data.model

data class Usuario(
    val id: Int,
    val nombre: String,
    val apellidoPaterno: String,
    val apellidoMaterno: String,
    val correo: String,
    val telefono: String,
    val idDepartamento: Int,
    val inicioJornada: String, // String representation of Time, e.g., "08:00"
    val finJornada: String     // String representation of Time, e.g., "16:00"
) {
    val nombreCompleto: String
        get() = "$nombre $apellidoPaterno $apellidoMaterno".trim()
}
