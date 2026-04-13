package mx.edu.utez.jyps.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import mx.edu.utez.jyps.data.model.JustificationResponse
import mx.edu.utez.jyps.data.model.PassResponse
import mx.edu.utez.jyps.data.network.RetrofitInstance
import mx.edu.utez.jyps.data.repository.JustificationRepository
import mx.edu.utez.jyps.data.repository.PassRepository
import timber.log.Timber

/**
 * UI State definition for the Manager's Incident Dashboard.
 */
sealed interface ManagerIncidentsUiState {
    /** Indicates data is currently being fetched or processed. */
    data object Loading : ManagerIncidentsUiState

    /** 
     * Represents the successful retrieval of incidents.
     * 
     * @property passes The list of passes pending or reviewed.
     * @property justifications The list of justifications pending or reviewed.
     */
    data class Success(
        val passes: List<PassResponse> = emptyList(),
        val justifications: List<JustificationResponse> = emptyList()
    ) : ManagerIncidentsUiState

    /** 
     * Represents a failure state with a user-friendly or technical message.
     * 
     * @property message The error explanation.
     */
    data class Error(val message: String) : ManagerIncidentsUiState
}

/**
 * ViewModel responsible for orchestrating data operations regarding employee incidents 
 * (Exit Passes and Justifications) specifically for a Department Head (Manager).
 * Uses manual instantiation to strictly follow project architecture.
 * 
 * @param application The application context needed for repository instantiation.
 */
class ManagerIncidentsViewModel(application: Application) : AndroidViewModel(application) {

    private val justificationRepository = JustificationRepository(RetrofitInstance.api, application)
    private val passRepository = PassRepository(RetrofitInstance.api)

    private val _uiState = MutableStateFlow<ManagerIncidentsUiState>(ManagerIncidentsUiState.Loading)
    val uiState: StateFlow<ManagerIncidentsUiState> = _uiState.asStateFlow()

    /**
     * Simultaneously fetches the exit passes and justifications assigned to the given manager.
     * 
     * @param jefeId The unique identifier of the manager executing the request.
     */
    fun fetchIncidents(jefeId: Long) {
        viewModelScope.launch {
            _uiState.value = ManagerIncidentsUiState.Loading
            
            val passesResult = passRepository.getPasesPorJefe(jefeId)
            val justificationsResult = justificationRepository.getJustificantesPorJefe(jefeId)

            if (passesResult.isSuccess && justificationsResult.isSuccess) {
                _uiState.value = ManagerIncidentsUiState.Success(
                    passes = passesResult.getOrDefault(emptyList()),
                    justifications = justificationsResult.getOrDefault(emptyList())
                )
            } else {
                val errorMsg = passesResult.exceptionOrNull()?.message 
                    ?: justificationsResult.exceptionOrNull()?.message 
                    ?: "Unknown error fetching incidents."
                _uiState.value = ManagerIncidentsUiState.Error(errorMsg)
                Timber.e("Error fetching manager incidents: $errorMsg")
            }
        }
    }

    /**
     * Submits a managerial review (approval/rejection) for a specific exit pass.
     * Refreshes the internal lists if the review was successful.
     * 
     * @param paseDeSalidaId The ID of the targeted pass.
     * @param estado The new workflow state (e.g., APROBADO, RECHAZADO).
     * @param comentario Explanatory feedback or observation provided by the manager.
     * @param jefeId The ID of the manager to re-fetch incidents after completion.
     */
    fun reviewPass(paseDeSalidaId: Long, estado: String, comentario: String?, jefeId: Long) {
        viewModelScope.launch {
            _uiState.value = ManagerIncidentsUiState.Loading
            val result = passRepository.revisarPase(paseDeSalidaId, estado, comentario)
            if (result.isSuccess) {
                Timber.d("Successfully reviewed pass $paseDeSalidaId")
                fetchIncidents(jefeId) // Refresh to see changes
            } else {
                val errorMsg = result.exceptionOrNull()?.message ?: "Failed to review pass"
                _uiState.value = ManagerIncidentsUiState.Error(errorMsg)
            }
        }
    }

    /**
     * Submits a managerial review (approval/rejection) for a specific justification.
     * Refreshes the internal lists if the review was successful.
     * 
     * @param justificanteId The ID of the targeted justification.
     * @param estado The new workflow state (e.g., APROBADO, RECHAZADO).
     * @param comentario Explanatory feedback or observation provided by the manager.
     * @param jefeId The ID of the manager to re-fetch incidents after completion.
     */
    fun reviewJustification(justificanteId: Long, estado: String, comentario: String?, jefeId: Long) {
        viewModelScope.launch {
            _uiState.value = ManagerIncidentsUiState.Loading
            val result = justificationRepository.revisarJustificante(justificanteId, estado, comentario)
            if (result.isSuccess) {
                Timber.d("Successfully reviewed justification $justificanteId")
                fetchIncidents(jefeId) // Refresh to see changes
            } else {
                val errorMsg = result.exceptionOrNull()?.message ?: "Failed to review justification"
                _uiState.value = ManagerIncidentsUiState.Error(errorMsg)
            }
        }
    }
}
