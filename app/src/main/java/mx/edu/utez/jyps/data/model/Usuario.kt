package mx.edu.utez.jyps.data.model

/**
 * Matches the server's UsuarioResponse JSON exactly.
 * horaEntrada / horaSalida come as Strings ("HH:mm:ss") from GET endpoints.
 *
 * @property id The unique identifier of the user.
 * @property nombreCompleto Real name of the employee or admin.
 * @property correo The institutional email address for login and notifications.
 * @property telefono User's contact phone number.
 * @property horaEntrada Formatted string representing standard clock-in time.
 * @property horaSalida Formatted string representing standard clock-out time.
 * @property roles Granted domain authorities logic access.
 * @property departamentoId Associated department structural identifier.
 * @property nombreDepartamento The readable name of the user's mapped department.
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
    val nombreDepartamento: String? = null
) {
    val isActivo: Boolean
        get() = true // Resolved via /cuenta endpoint

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
        get() = nombreCompleto.firstOrNull()?.uppercase() ?: "?"

    /** Converts "HH:mm:ss" to "H:mm AM/PM" */
    val horaEntradaDisplay: String
        get() = horaEntrada?.toAmPm() ?: "--:--"

    val horaSalidaDisplay: String
        get() = horaSalida?.toAmPm() ?: "--:--"

    val departamentoDisplay: String
        get() = nombreDepartamento ?: if (departamentoId > 0) "Depto. $departamentoId" else "Sin departamento"

    /** Parse "HH:mm:ss" to hour/minute pair */
    val entradaHour: Int get() = horaEntrada?.split(":")?.getOrNull(0)?.toIntOrNull() ?: 8
    val entradaMinute: Int get() = horaEntrada?.split(":")?.getOrNull(1)?.toIntOrNull() ?: 0
    val salidaHour: Int get() = horaSalida?.split(":")?.getOrNull(0)?.toIntOrNull() ?: 16
    val salidaMinute: Int get() = horaSalida?.split(":")?.getOrNull(1)?.toIntOrNull() ?: 0
}

private fun String.toAmPm(): String {
    val parts = split(":")
    val h24 = parts.getOrNull(0)?.toIntOrNull() ?: return this
    val min = parts.getOrNull(1)?.toIntOrNull() ?: 0
    val h = if (h24 == 0) 12 else if (h24 > 12) h24 - 12 else h24
    val amPm = if (h24 < 12) "AM" else "PM"
    return "%d:%02d %s".format(h, min, amPm)
}

/**
 * Used only for POST/PUT requests where the server expects { hour, minute, second, nano }.
 *
 * @property hour Hours component of the time.
 * @property minute Minutes component of the time.
 * @property second Seconds component of the time.
 * @property nano Nanoseconds component.
 */
data class LocalTimeInfo(
    @com.google.gson.annotations.SerializedName("hour")
    val hour: Int = 0,
    @com.google.gson.annotations.SerializedName("minute")
    val minute: Int = 0,
    @com.google.gson.annotations.SerializedName("second")
    val second: Int = 0,
    @com.google.gson.annotations.SerializedName("nano")
    val nano: Int = 0
) {
    fun toDisplayString(): String {
        val h = if (hour == 0) 12 else if (hour > 12) hour - 12 else hour
        val amPm = if (hour < 12) "AM" else "PM"
        return "%d:%02d %s".format(h, minute, amPm)
    }
}

/**
 * Matches CuentaResponse: { nombreCompleto, activa, intentosFallidos, bloqueada }
 *
 * @property nombreCompleto Real name mapped from the identity.
 * @property activa Defines if the account credential logic is allowed.
 * @property intentosFallidos Current sequential failed login attempt count.
 * @property bloqueada Marks if the account is blocked due to excessive failures or bans.
 */
data class CuentaResponse(
    val nombreCompleto: String = "",
    val activa: Boolean = false,
    val intentosFallidos: Int = 0,
    val bloqueada: Boolean = false
)

/**
 * Matches EstadoCuentaResponse: { nombreCompleto, activa, message }
 *
 * @property nombreCompleto Real name associated with the account.
 * @property activa Defines if the account is active.
 * @property message System message indicating current status restrictions.
 */
data class EstadoCuentaResponse(
    val nombreCompleto: String = "",
    val activa: Boolean = false,
    val message: String = ""
)
