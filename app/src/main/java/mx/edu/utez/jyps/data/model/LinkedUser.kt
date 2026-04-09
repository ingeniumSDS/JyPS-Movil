package mx.edu.utez.jyps.data.model

/**
 * Represents a user linked to a specific department.
 * Used primarily for warning dialogs during department deactivation.
 *
 * @property id Unique identifier of the user.
 * @property fullName Full name of the user.
 * @property role Role or position within the department (e.g., "Trabajador").
 * @property isActive Whether the user's account is currently active.
 */
data class LinkedUser(
    val id: Long,
    val fullName: String,
    val role: String,
    val isActive: Boolean = true
)
