package mx.edu.utez.jyps.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import mx.edu.utez.jyps.data.network.RetrofitInstance
import mx.edu.utez.jyps.data.repository.AuthRepository
import mx.edu.utez.jyps.data.repository.PreferencesManager

/**
 * Represents the unified state of an active user session.
 */
data class SessionState(
    val isLoggedIn: Boolean = false,
    val roles: List<String> = emptyList(),
    val userName: String = "Usuario",
    val userEmail: String = "",
    val userPhone: String = "No disponible"
)

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val preferencesManager = PreferencesManager(application)
    private val repository = AuthRepository(RetrofitInstance.api, preferencesManager)

    /**
     * Unified reactive flow for the entire session state.
     * Consuming this ensures atomic updates and prevents UI flickering.
     */
    val sessionState: StateFlow<SessionState> = kotlinx.coroutines.flow.combine(
        repository.tokenFlow,
        repository.rolesFlow,
        preferencesManager.userProfileFlow
    ) { token, roles, profile ->
        SessionState(
            isLoggedIn = !token.isNullOrEmpty(),
            roles = roles?.split(",")?.filter { it.isNotEmpty() } ?: emptyList(),
            userName = profile.first,
            userEmail = profile.second,
            userPhone = profile.third
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SessionState())

    // Convenience properties for backwards compatibility or granular observation
    val isLoggedIn: StateFlow<Boolean> = sessionState.map { it.isLoggedIn }
        .distinctUntilChanged()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val currentRoles: StateFlow<List<String>> = sessionState.map { it.roles }
        .distinctUntilChanged()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userName: StateFlow<String> = sessionState.map { it.userName }
        .distinctUntilChanged()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "Usuario")

    val userEmail: StateFlow<String> = sessionState.map { it.userEmail }
        .distinctUntilChanged()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    val userPhone: StateFlow<String> = sessionState.map { it.userPhone }
        .distinctUntilChanged()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "No disponible")

    private val _correo = MutableStateFlow("")
    val correo: StateFlow<String> = _correo

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun onCorreoChange(value: String) { _correo.value = value }
    fun onPasswordChange(value: String) { _password.value = value }

    fun login() {
        val email = _correo.value.trim()
        val pwd = _password.value.trim()

        if (email.isBlank() || pwd.isBlank()) {
            _errorMessage.value = "Por favor ingresa correo y contraseña" 
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val result = repository.login(email, pwd)
            result.onFailure { error ->
                _errorMessage.value = error.message
            }
            _isLoading.value = false
        }
    }

    /**
     * Purges all session data and tokens, triggering a global navigation reset to the Login screen.
     */
    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }
}
