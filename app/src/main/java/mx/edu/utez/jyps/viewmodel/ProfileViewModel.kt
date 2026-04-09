package mx.edu.utez.jyps.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * UI State for the Profile screen.
 * 
 * @property name User's full name.
 * @property role User's occupation/role description.
 * @property email Institutional email address.
 * @property phone Contact phone number.
 * @property showChangePasswordDialog Indicates if the modal to change password is open.
 * @property isPasswordOpSuccess Indicates if the password modification succeeded.
 * @property passwordOpMessage Descriptive message to show in the Toast.
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
 * ViewModel managing employee profile information and session actions.
 */
class ProfileViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    /** Prompts the password modification modal. */
    fun changePassword() {
        _uiState.value = _uiState.value.copy(showChangePasswordDialog = true)
    }

    /** Hides the password modification modal. */
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
     * Updates the session user information.
     * 
     * @param name Full name to display.
     * @param email Institutional email address.
     * @param role User's occupation/role description.
     */
    fun setUserInfo(name: String, email: String, role: String) {
        _uiState.value = _uiState.value.copy(name = name, email = email, role = role)
    }

    /** Clears any active notification overlay. */
    fun clearOpMessage() {
        _uiState.value = _uiState.value.copy(isPasswordOpSuccess = false, passwordOpMessage = null)
    }
}
