package mx.edu.utez.jyps.data.model

data class User( // De momento solo sirve de referencia
    var id: Int = 0,
    var nombre: String = "",
    var imagen: String? = null, // URL de la imagen desde el servidor
    var descripcion: String = "",
    var numPases: Int = 0,
    var fechaPase: String = "",
)