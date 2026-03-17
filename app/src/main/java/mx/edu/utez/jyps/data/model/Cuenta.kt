package mx.edu.utez.jyps.data.model

data class Cuenta(
    val idUsuario: Int,
    val intentosFallidos: Int,
    val tokenRecuperacion: String?,
    val tokenExpiresAt: String?, // ISO-8601 string or similar
    val tokenUsado: Boolean,
    val bloqueada: Boolean,
    val blockedAt: String?,
    val activa: Boolean,
    val passwordHash: String // simplified for ERD's password
)
