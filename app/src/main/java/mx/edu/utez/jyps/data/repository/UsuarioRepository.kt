package mx.edu.utez.jyps.data.repository

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import mx.edu.utez.jyps.data.model.Usuario
import mx.edu.utez.jyps.data.model.UserRequest
import mx.edu.utez.jyps.data.network.ApiService
import retrofit2.Response

class UsuarioRepository(
    private val apiService: ApiService
) {
    private val _allUsers = MutableStateFlow<List<Usuario>>(emptyList())
    val allUsers: Flow<List<Usuario>> = _allUsers.asStateFlow()

    private val _selectedUser = MutableStateFlow<Usuario?>(null)
    val selectedUser: Flow<Usuario?> = _selectedUser.asStateFlow()

    suspend fun getUsuarios(): List<Usuario> {
        return try {
            Log.d("UsuarioRepository", "Iniciando petición GET /api/v1/usuarios")
            val response = apiService.getUsuarios()
            Log.d("UsuarioRepository", "Respuesta recibida: ${response.size} usuarios")
            _allUsers.value = response
            response
        } catch (e: Exception) {
            Log.e("UsuarioRepository", "Error al obtener usuarios", e)
            emptyList()
        }
    }

    suspend fun getUsuarioPorId(id: Long): Usuario? {
        return try {
            Log.d("UsuarioRepository", "Iniciando petición GET /api/v1/usuarios/$id")
            val response = apiService.getUsuarioPorId(id)
            _selectedUser.value = response
            response
        } catch (e: Exception) {
            Log.e("UsuarioRepository", "Error al obtener usuario por ID", e)
            // Fallback: buscar en la lista local cacheada
            _allUsers.value.find { it.id == id }?.also { _selectedUser.value = it }
        }
    }

    suspend fun registrarUsuario(request: UserRequest): Response<Void> {
        return try {
            Log.d("UsuarioRepository", "Iniciando petición POST /api/v1/usuarios")
            val response = apiService.registrarUsuario(request)
            if (response.isSuccessful) {
                Log.d("UsuarioRepository", "Usuario registrado exitosamente")
                // Refrescar la lista tras el registro
                getUsuarios()
            } else {
                Log.e("UsuarioRepository", "Error en el registro: ${response.code()}")
            }
            response
        } catch (e: Exception) {
            Log.e("UsuarioRepository", "Excepción al registrar usuario", e)
            throw e
        }
    }

    fun clearSelectedUser() {
        _selectedUser.value = null
    }
}
