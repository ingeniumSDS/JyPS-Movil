package mx.edu.utez.jyps.data.model

/**
 * Matches the updated server's UsuarioResponse JSON.
 * High-performance mapping that includes account status and roles directly.
 *
 * @property id The unique identifier of the user.
 * @property nombreCompleto Real name of the employee or admin.
 * @property correo The institutional email address for login and notifications.
 * @property telefono User's contact phone number.
 * @property roles List of granted roles (e.g., ["ADMINISTRADOR", "EMPLEADO"]).
 * @property departamentoId Associated department structural identifier.
 * @property nombreDepartamento The readable name of the user's mapped department.
 * @property activo Defines if the user account is currently enabled.
 */
data class Usuario(
    val id: Long = 0,
    val nombreCompleto: String = "",
    val correo: String = "",
    val telefono: String = "",
    val horaEntrada: String? = null,
    val horaSalida: String? = null,
    val roles: List<String> = emptyList(),
    val departamentoId: Long = 0,
    val nombreDepartamento: String? = null,
    val activo: Boolean = true
) {
    /** First primary role assigned to the user. */
    val primaryRole: String
        get() = roles.firstOrNull() ?: ""

    /** Human-readable display string for the primary role. */
    val primaryRoleDisplay: String
        get() = when (primaryRole) {
            "EMPLEADO" -> "Empleado"
            "GUARDIA" -> "Guardia"
            "JEFE_DE_DEPARTAMENTO" -> "Jefe de Departamento"
            "ADMINISTRADOR" -> "Administrador"
            "AUDITOR" -> "Auditor"
            else -> primaryRole
        }

    /** First letter of the user's name for avatar visualization. */
    val initial: String
        get() = nombreCompleto.firstOrNull()?.uppercase() ?: "?"

    /** Converts "HH:mm:ss" to human-readable "H:mm AM/PM". */
    val horaEntradaDisplay: String
        get() = horaEntrada?.toAmPm() ?: "--:--"

    /** Converts "HH:mm:ss" to human-readable "H:mm AM/PM". */
    val horaSalidaDisplay: String
        get() = horaSalida?.toAmPm() ?: "--:--"

    /** Returns the department name or a placeholder if missing. */
    val departamentoDisplay: String
        get() = nombreDepartamento ?: if (departamentoId > 0) "Depto. $departamentoId" else "Sin departamento"

    /** Extracted hours for form editing logic. */
    val entradaHour: Int get() = horaEntrada?.split(":")?.getOrNull(0)?.toIntOrNull() ?: 8
    val entradaMinute: Int get() = horaEntrada?.split(":")?.getOrNull(1)?.toIntOrNull() ?: 0
    val salidaHour: Int get() = horaSalida?.split(":")?.getOrNull(0)?.toIntOrNull() ?: 16
    val salidaMinute: Int get() = horaSalida?.split(":")?.getOrNull(1)?.toIntOrNull() ?: 0
}

/**
 * Extension to convert 24h string to 12h formatting.
 */
private fun String.toAmPm(): String {
    val parts = split(":")
    val h24 = parts.getOrNull(0)?.toIntOrNull() ?: return this
    val min = parts.getOrNull(1)?.toIntOrNull() ?: 0
    val h = if (h24 == 0) 12 else if (h24 > 12) h24 - 12 else h24
    val amPm = if (h24 < 12) "AM" else "PM"
    return "%d:%02d %s".format(h, min, amPm)
}

/**
 * Encapsulates security account details from /usuarios/{id}/cuenta.
 *
 * @property nombreCompleto Real name from the identity.
 * @property activa Defines if the account credential logic is allowed.
 * @property intentosFallidos Current sequential failed login attempt count.
 * @property bloqueada Marks if the account is blocked due to excessive failures.
 */
data class CuentaResponse(
    val nombreCompleto: String = "",
    val activa: Boolean = false,
    val intentosFallidos: Int = 0,
    val bloqueada: Boolean = false
)

/**
 * Encapsulates the response from account status toggle operations.
 */
data class EstadoCuentaResponse(
    val nombreCompleto: String = "",
    val activa: Boolean = false,
    val message: String = ""
)
