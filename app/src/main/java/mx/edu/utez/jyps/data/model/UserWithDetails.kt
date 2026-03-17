package mx.edu.utez.jyps.data.model

/**
 * Composite model used by the ViewModel and UI to display
 * complete information about a User without needing joint queries
 * in the UI layer.
 */
data class UserWithDetails(
    val usuario: Usuario,
    val cuenta: Cuenta,
    val departamento: Departamento,
    val roles: List<Roles>
) {
    // Helper property to get the primary role for display.
    val primaryRole: Roles?
        get() = roles.firstOrNull()
}
