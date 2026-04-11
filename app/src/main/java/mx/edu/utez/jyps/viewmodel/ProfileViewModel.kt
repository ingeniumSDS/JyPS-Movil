package mx.edu.utez.jyps.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * UI State for the Profile Management interface.
 * 
 * @param name The full display name of the authenticated identity.
 * @param role The institutional role designation (e.g., Empleado, Administrador).
 * @param email Institutional contact email associated with the session.
 * @param phone Contact telephone number for verified identity.
 * @param showChangePasswordDialog Atomic toggle for the credential update modal.
 * @param isPasswordOpSuccess Result flag for the last password modification attempt.
 * @param passwordOpMessage Diagnostic feedback message for user notifications.
 */
data class ProfileUiState(
    val name: String = "Juan Pérez García",
    val role: String = "Empleado",
    val email: String = "juan.perez@utez.edu.mx",
    val phone: String = "777-123-4567",
    val showChangePasswordDialog: Boolean = false,
    val isPasswordOpSuccess: Boolean = false,
    val passwordOpMessage: String? = null
)

/**
 * ViewModel orchestrating the personal profile settings and credential rotations.
 * 
 * Acts as the state authority for user-specific identity visualization and enforces 
 * client-side validation for security-sensitive operations like password updates.
 */
class ProfileViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    /**
     * Dispatches a request to display the credential update interface.
     */
    fun changePassword() {
        _uiState.value = _uiState.value.copy(showChangePasswordDialog = true)
    }

    /**
     * Revokes the visibility of the credential update interface.
     */
    fun dismissChangePassword() {
        _uiState.value = _uiState.value.copy(showChangePasswordDialog = false)
    }

    /**
     * Attempts to replace the user's password with a new one.
     *
     * @param current The current password used for validation.
     * @param new The desired new password.
     * @param confirm The confirmation of the new password.
     */
    fun updatePassword(current: String, new: String, confirm: String) {
        val dummyAuth = "Actual123"
        // Dummy Validation (Backend-side emulated)
        if (current != dummyAuth) {
            _uiState.value = _uiState.value.copy(passwordOpMessage = "Contraseña actual incorrecta (Usar: Actual123)")
            return
        }

        _uiState.value = _uiState.value.copy(
            showChangePasswordDialog = false,
            isPasswordOpSuccess = true,
            passwordOpMessage = "Contraseña actualizada exitosamente."
        )
    }

    /**
     * Propagates session identity updates to the reactive UI state.
     * 
     * @param name Validated display name from the identity provider.
     * @param email Verified institutional email.
     * @param phone Registered contact phone.
     * @param role Authorized operational role title.
     */
    fun setUserInfo(name: String, email: String, phone: String, role: String) {
        _uiState.value = _uiState.value.copy(name = name, email = email, phone = phone, role = role)
    }

    /**
     * Purges active diagnostic messages from the notification overlay.
     */
    fun clearOpMessage() {
        _uiState.value = _uiState.value.copy(isPasswordOpSuccess = false, passwordOpMessage = null)
    }
}
