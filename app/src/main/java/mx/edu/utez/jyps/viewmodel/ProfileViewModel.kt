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
    val role: String = "Trabajador",
    val email: String = "juan.perez@utez.edu.mx",
    val phone: String = "777-123-4567"
)

/**
 * ViewModel managing employee profile information and session actions.
 */
class ProfileViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    /**
     * Dummy function to simulate password change request.
     */
    fun changePassword() {
        // Implementation for future sprint
    }
}
