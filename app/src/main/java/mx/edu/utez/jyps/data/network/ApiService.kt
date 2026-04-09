package mx.edu.utez.jyps.data.network

import mx.edu.utez.jyps.data.model.CuentaResponse
import mx.edu.utez.jyps.data.model.Departamento
import mx.edu.utez.jyps.data.model.EstadoCuentaResponse
import mx.edu.utez.jyps.data.model.LoginRequest
import mx.edu.utez.jyps.data.model.LoginResponse
import mx.edu.utez.jyps.data.model.Usuario
import mx.edu.utez.jyps.data.model.UserRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

/**
 * Central interface defining all Retrofit HTTP operations.
 * Maps the mobile app requests directly to the REST backend endpoints.
 */
interface ApiService {

    // ── Autenticación ───────────────────────────────
    @POST("api/v1/usuarios/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    // ── Usuarios ────────────────────────────────────
    @GET("api/v1/usuarios")
    suspend fun getUsuarios(): List<Usuario>

    @GET("api/v1/usuarios/{id}")
    suspend fun getUsuarioPorId(@Path("id") id: Long): Usuario

    @POST("api/v1/usuarios")
    suspend fun registrarUsuario(@Body request: UserRequest): Response<Usuario>

    @PUT("api/v1/usuarios/{id}")
    suspend fun actualizarUsuario(@Path("id") id: Long, @Body request: UserRequest): Response<Usuario>

    @PATCH("api/v1/usuarios/{id}/estado")
    suspend fun toggleEstadoUsuario(@Path("id") id: Long): Response<EstadoCuentaResponse>

    @GET("api/v1/usuarios/{id}/cuenta")
    suspend fun getCuentaUsuario(@Path("id") id: Long): CuentaResponse

    // ── Departamentos ──────────────────────────────
    @GET("api/v1/departamentos")
    suspend fun getDepartamentos(): List<Departamento>
}