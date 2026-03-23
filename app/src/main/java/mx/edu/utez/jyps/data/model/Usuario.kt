package mx.edu.utez.jyps.data.model

/**
 * Modelo único de Usuario, mapeado directamente desde la respuesta JSON del servidor.
 * Sigue el mismo patrón que GameConsole en el proyecto de referencia.
 */
data class Usuario(
    val id: Long = 0,
    val nombre: String = "",
    val apellidoPaterno: String = "",
    val apellidoMaterno: String = "",
    val correo: String = "",
    val telefono: String = "",
    val horaEntrada: String? = null, // null si el rol no es EMPLEADO
    val horaSalida: String? = null,
    val roles: List<String> = emptyList(), // e.g. ["EMPLEADO", "ADMINISTRADOR"]
    val departamentoId: Long = 0,
    val cuenta: CuentaInfo? = null
) {
    val nombreCompleto: String
        get() = "$nombre $apellidoPaterno $apellidoMaterno".trim()

    val isActivo: Boolean
        get() = cuenta?.activa ?: false

    val primaryRole: String
        get() = roles.firstOrNull() ?: ""

    val primaryRoleDisplay: String
        get() = when (primaryRole) {
            "EMPLEADO" -> "Empleado"
            "GUARDIA" -> "Guardia"
            "JEFE_DE_DEPARTAMENTO" -> "Jefe de Departamento"
            "ADMINISTRADOR" -> "Administrador"
            "AUDITOR" -> "Auditor"
            else -> primaryRole
        }

    val initial: String
        get() = nombre.firstOrNull()?.uppercase() ?: "?"
}

/**
 * Datos de cuenta anidados en la respuesta del servidor.
 */
data class CuentaInfo(
    val activa: Boolean = false,
    val bloqueada: Boolean = false,
    val intentosFallidos: Int = 0,
    val tokenUsado: Boolean = false,
    val tokenRecuperacion: String? = null,
    val tokenExpiresAt: String? = null,
    val blockedAt: String? = null
)
