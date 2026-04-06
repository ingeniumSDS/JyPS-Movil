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

    fun changePassword() {
        _uiState.value = _uiState.value.copy(showChangePasswordDialog = true)
    }

    fun dismissChangePassword() {
        _uiState.value = _uiState.value.copy(showChangePasswordDialog = false)
    }

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

    fun clearOpMessage() {
        _uiState.value = _uiState.value.copy(isPasswordOpSuccess = false, passwordOpMessage = null)
    }
}
