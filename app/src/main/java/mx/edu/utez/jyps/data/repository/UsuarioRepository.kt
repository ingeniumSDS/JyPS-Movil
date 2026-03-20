package mx.edu.utez.jyps.data.repository

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import mx.edu.utez.jyps.data.model.UserRequest
import mx.edu.utez.jyps.data.model.UserWithDetails
import mx.edu.utez.jyps.data.network.ApiService
import retrofit2.Response

class UsuarioRepository(
    private val apiService: ApiService
) {
    private val _allUsers = MutableStateFlow<List<UserWithDetails>>(emptyList())
    val allUsers: Flow<List<UserWithDetails>> = _allUsers.asStateFlow()

    suspend fun getUsuarios(): List<UserWithDetails> {
        return try {
            Log.d("UsuarioRepository", "Iniciando petición GET /usuarios")
            val users = apiService.getUsuarios()
            _allUsers.value = users
            users
        } catch (e: Exception) {
            Log.e("UsuarioRepository", "Error al obtener usuarios", e)
            emptyList()
        }
    }

    suspend fun registrarUsuario(request: UserRequest): Response<Void> {
        return try {
            Log.d("UsuarioRepository", "Iniciando petición POST /api/v1/usuarios")
            val response = apiService.registrarUsuario(request)
            
            if (response.isSuccessful) {
                Log.d("UsuarioRepository", "Usuario registrado exitosamente")
                // El server no regresa el objeto, se actualiza localmente en el ViewModel para el demo.
            } else {
                Log.e("UsuarioRepository", "Error en el registro: ${response.code()}")
            }
            response
        } catch (e: Exception) {
            Log.e("UsuarioRepository", "Excepción al registrar usuario", e)
            throw e
        }
    }

    // Función para actualizar la lista local manualmente (como pidió el usuario para el demo)
    fun updateLocalList(newUser: UserWithDetails) {
        _allUsers.value = listOf(newUser) + _allUsers.value
    }
    
    fun setInitialMockData(mockData: List<UserWithDetails>) {
        _allUsers.value = mockData
    }
}
