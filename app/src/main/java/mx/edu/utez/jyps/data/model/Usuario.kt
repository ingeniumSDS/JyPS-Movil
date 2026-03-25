package mx.edu.utez.jyps.data.model

/**
 * Matches the server's UsuarioResponse JSON exactly.
 * horaEntrada / horaSalida come as Strings ("HH:mm:ss") from GET endpoints.
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
 */
data class CuentaResponse(
    val nombreCompleto: String = "",
    val activa: Boolean = false,
    val intentosFallidos: Int = 0,
    val bloqueada: Boolean = false
)

/**
 * Matches EstadoCuentaResponse: { nombreCompleto, activa, message }
 */
data class EstadoCuentaResponse(
    val nombreCompleto: String = "",
    val activa: Boolean = false,
    val message: String = ""
)
