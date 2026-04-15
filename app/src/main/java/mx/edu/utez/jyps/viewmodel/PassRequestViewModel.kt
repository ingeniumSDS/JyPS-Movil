package mx.edu.utez.jyps.viewmodel

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import mx.edu.utez.jyps.data.model.PassRequest
import mx.edu.utez.jyps.data.network.RetrofitInstance
import mx.edu.utez.jyps.data.repository.PassRepository
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

/**
 * State class for the Pass Request screen.
 *
 * @property fullName Automatically injected from user session.
 * @property email Automatically injected from user session.
 * @property selectedDate The designated exit day (default: today).
 * @property selectedTime Transformed instance tracking hour/minute choice.
 * @property details Textual details describing the reasoning.
 * @property detailsMinLimit Hard validation limit for UI bounds check.
 * @property detailsLimit Hard absolute limit.
 * @property showTimePicker Flags the display sequence of the material TimePicker.
 * @property isLoading Indicates if network submission is blocking UI.
 * @property isSuccess Lifecycle state evaluating to correct completion.
 * @property hasActivePassError True if user violates DFR limit per day.
 * @property error Active error message string if request crashed.
 */
data class PassRequestState(
    val fullName: String = "Juan Pérez García", // Injected from session in real app
    val email: String = "juan.perez@utez.edu.mx", // Injected from session in real app
    val selectedDate: LocalDate = LocalDate.now(),
    val selectedTime: LocalTime? = null,
    val details: String = "",
    val detailsMinLimit: Int = 25,
    val detailsLimit: Int = 255,
    val showTimePicker: Boolean = false,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val hasActivePassError: Boolean = false,
    val error: String? = null,
    val jefeId: Long = 0
) {
    val dateDisplay: String
        get() = selectedDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))

    val timeDisplay: String
        get() = selectedTime?.format(DateTimeFormatter.ofPattern("hh:mm a")) ?: ""

    val isFormValid: Boolean
        get() = !isLoading && selectedTime != null && details.length in detailsMinLimit..detailsLimit
}

/**
 * ViewModel responsible for the logic of requesting a Pass.
 */
class PassRequestViewModel(application: android.app.Application) : AndroidViewModel(application) {
    private val preferencesManager = mx.edu.utez.jyps.data.repository.PreferencesManager(application)
    private val repository = PassRepository(RetrofitInstance.api)
    private val _uiState = MutableStateFlow(PassRequestState())
    val uiState: StateFlow<PassRequestState> = _uiState.asStateFlow()

    /** Primary key identifier for the logged user, used for real API calls. */
    private var userId: Long = 0
    private var jefeId: Long = 0

    init {
        checkActivePasses()
        viewModelScope.launch {
            preferencesManager.deptIdFlow.collect { id ->
                this@PassRequestViewModel.jefeId = id
                _uiState.update { it.copy(jefeId = id) }
                timber.log.Timber.d("PassRequestVM: Automatically loaded JefeId=$id from preferences")
            }
        }
        viewModelScope.launch {
            preferencesManager.userIdFlow.collect { id ->
                this@PassRequestViewModel.userId = id
                timber.log.Timber.d("PassRequestVM: Automatically loaded UserId=$id from preferences")
            }
        }
    }

    /**
     * Bootstraps session info into the request template.
     *
     * @param name Name representing the auth.
     * @param email Verified email.
     * @param id The internal database ID of the employee.
     */
    fun setUserInfo(name: String, email: String, id: Long = 0, jefeId: Long = 0) {
        this.userId = id
        this.jefeId = jefeId
        timber.log.Timber.d("PassRequestVM: User=$id, Jefe=$jefeId, Name=$name")
        _uiState.update { it.copy(fullName = name, email = email, jefeId = jefeId) }
    }

    private fun checkActivePasses() {
        // Dummy check for DFR rule: "el sistema impide la creación de un nuevo pase únicamente si ya existe una solicitud..."
        val hasActivePass = false // Toggle this to test the blockade
        if (hasActivePass) {
            _uiState.update { 
                it.copy(
                    hasActivePassError = true, 
                    error = "Aún cuenta con una Solicitud de Pase de salida pendiente para el día de hoy"
                )
            }
        }
    }

    /** Engages the material OS time selection widget. */
    fun onTimeClick() {
        _uiState.update { it.copy(showTimePicker = true) }
    }

    /** Hides the time selection widget natively. */
    fun onTimeDismiss() {
        _uiState.update { it.copy(showTimePicker = false) }
    }

    /**
     * Locks in the time choice picked by the user format.
     *
     * @param time Explicit Java time construct.
     */
    fun onTimeSelected(time: LocalTime) {
        _uiState.update { 
            it.copy(
                selectedTime = time,
                showTimePicker = false
            )
        }
    }

    /**
     * Appends reason text evaluating bounds checking continuously.
     *
     * @param newDetails Raw input string snippet.
     */
    fun onDetailsChanged(newDetails: String) {
        if (newDetails.length <= _uiState.value.detailsLimit) {
            _uiState.update { it.copy(details = newDetails) }
        }
    }

    /**
     * Assembles payload and executes the network request or mock simulator.
     */
    fun onSubmit() {
        if (!_uiState.value.isFormValid) return
        
        if (_uiState.value.jefeId == 0L) {
            _uiState.update { it.copy(error = "No tienes un jefe encargado asignado. Contacta con administración.") }
            return
        }
        
        _uiState.update { it.copy(isLoading = true, error = null) }
        
        // Mantener lógica mocked para el usuario de demostración solicitado
        if (_uiState.value.email == "juan.perez@utez.edu.mx") {
            viewModelScope.launch {
                delay(1200) // Simular latencia de red
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        isSuccess = true
                    ) 
                }
            }
            return
        }

        // Lógica real para usuarios ordinarios
        viewModelScope.launch {
            val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")
            val passRequest = PassRequest(
                empleadoId = userId,
                jefeId = jefeId,
                horaSolicitada = "${_uiState.value.selectedDate}T${_uiState.value.selectedTime?.format(timeFormatter) ?: "00:00:00"}",
                fechaSolicitud = _uiState.value.selectedDate.toString(),
                descripcion = _uiState.value.details
            )

            repository.crearPase(passRequest).onSuccess {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        isSuccess = true
                    ) 
                }
            }.onFailure { e ->
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "No se pudo enviar la solicitud. Intenta de nuevo."
                    ) 
                }
            }
        }
    }

    /** Dips error state back to clean canvas constraints. */
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    /** Resets blockade constraints. */
    fun clearActivePassError() {
        _uiState.update { it.copy(hasActivePassError = false) }
    }

    /** Destroys active completion signals. */
    fun resetSuccess() {
        _uiState.update { it.copy(isSuccess = false) }
    }
}
