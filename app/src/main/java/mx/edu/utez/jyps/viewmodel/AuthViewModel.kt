package mx.edu.utez.jyps.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import mx.edu.utez.jyps.data.network.RetrofitInstance
import mx.edu.utez.jyps.data.repository.AuthRepository
import mx.edu.utez.jyps.data.repository.PreferencesManager

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val preferencesManager = PreferencesManager(application)
    private val repository = AuthRepository(RetrofitInstance.api, preferencesManager)

    // Token Reactivo para la Navegación (Si hay token, isLoggedIn = true)
    val isLoggedIn: StateFlow<Boolean> = repository.tokenFlow
        .map { !it.isNullOrEmpty() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    // Formulario de login
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

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }
}
