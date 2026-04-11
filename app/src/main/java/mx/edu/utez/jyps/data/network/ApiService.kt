package mx.edu.utez.jyps.data.network

import mx.edu.utez.jyps.data.model.CuentaResponse
import mx.edu.utez.jyps.data.model.DepartamentoResponse
import mx.edu.utez.jyps.data.model.CreateDepartmentRequest
import mx.edu.utez.jyps.data.model.ToggleStatusRequest
import mx.edu.utez.jyps.data.model.EstadoCuentaResponse
import mx.edu.utez.jyps.data.model.LoginRequest
import mx.edu.utez.jyps.data.model.LoginResponse
import mx.edu.utez.jyps.data.model.UpdateDepartmentRequest
import mx.edu.utez.jyps.data.model.Usuario
import mx.edu.utez.jyps.data.model.UserRequest
import mx.edu.utez.jyps.data.model.PasswordTokenRequest
import mx.edu.utez.jyps.data.model.PasswordSetupRequest
import mx.edu.utez.jyps.data.model.GenericMessageResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Central interface defining all Retrofit HTTP operations.
 * Maps the mobile app requests directly to the REST backend endpoints.
 */
interface ApiService {

    /**
     * Authenticates a user based on institutional credentials.
     * 
     * @param request The [LoginRequest] containing email and password.
     * @return [Response] wrapping a [LoginResponse] with JWT if successful.
     */
    @POST("api/v1/usuarios/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    /**
     * Validates a temporary setup token for onboarding or password reset.
     * 
     * @param token The raw token string sent via email.
     * @return [Response] with [ResponseBody] indicating validation success.
     */
    @GET("api/v1/usuarios/setup/validar")
    suspend fun validarSetupToken(@Query("token") token: String): Response<ResponseBody>

    /**
     * Triggers the generation of a password recovery or setup token.
     * 
     * @param request [PasswordTokenRequest] containing the target user's identifier.
     * @return [Response] with [GenericMessageResponse] confirming intent.
     */
    @POST("api/v1/usuarios/token")
    suspend fun generarRecuperacionToken(@Body request: PasswordTokenRequest): Response<GenericMessageResponse>

    /**
     * Finalizes the password establishment process using a valid setup token.
     * 
     * @param request [PasswordSetupRequest] containing new credential and token.
     * @return [Response] with [CuentaResponse] confirming final account state.
     */
    @POST("api/v1/usuarios/setup")
    suspend fun establecerPassword(@Body request: PasswordSetupRequest): Response<CuentaResponse>

    /**
     * Retrieves the complete register of all users in the system.
     * 
     * @return Full [List] of [Usuario] objects.
     */
    @GET("api/v1/usuarios")
    suspend fun getUsuarios(): List<Usuario>

    /**
     * Filters users belonging to a specific department.
     * 
     * @param departamentoId The ID of the department to query.
     * @return [List] of [Usuario] filtered by the department owner.
     */
    @GET("api/v1/{id}/usuarios")
    suspend fun getUsuariosByDepartamento(@Path("id") departamentoId: Long): List<Usuario>

    /**
     * Retrieves users eligible to be assigned as department heads.
     * 
     * @return [List] of [Usuario] with specific administrative clearance.
     */
    @GET("api/v1/usuarios/jefes")
    suspend fun getJefesDisponibles(): List<Usuario>

    /**
     * Fetches detailed data for a unique user.
     * 
     * @param id The primary key identifier.
     * @return [Usuario] data model.
     */
    @GET("api/v1/usuarios/{id}")
    suspend fun getUsuarioPorId(@Path("id") id: Long): Usuario

    /**
     * Registers a new user identity in the central database.
     * 
     * @param request [UserRequest] with profile and role details.
     * @return [Response] wrapping the created [Usuario].
     */
    @POST("api/v1/usuarios")
    suspend fun registrarUsuario(@Body request: UserRequest): Response<Usuario>

    /**
     * Modifies an existing user's profile and settings.
     * 
     * @param id The target user ID.
     * @param request [UserRequest] containing the payload of changes.
     * @return [Response] wrapping the updated [Usuario].
     */
    @PUT("api/v1/usuarios/{id}")
    suspend fun actualizarUsuario(@Path("id") id: Long, @Body request: UserRequest): Response<Usuario>

    /**
     * Toggles the account's active/inactive status.
     * 
     * @param id The target user ID.
     * @return [Response] wrapping the new [EstadoCuentaResponse].
     */
    @PATCH("api/v1/usuarios/{id}/estado")
    suspend fun toggleEstadoUsuario(@Path("id") id: Long): Response<EstadoCuentaResponse>

    /**
     * Retrives technical account security details (failed attempts, block state).
     * 
     * @param id The target user ID.
     * @return [CuentaResponse] with security metrics.
     */
    @GET("api/v1/usuarios/{id}/cuenta")
    suspend fun getCuentaUsuario(@Path("id") id: Long): CuentaResponse

    /**
     * Retrieves all structural departments within the organization.
     * 
     * @return [List] of [DepartamentoResponse].
     */
    @GET("api/v1/departamentos")
    suspend fun getDepartamentos(): List<DepartamentoResponse>

    /**
     * Assigns a specific user as the head of a department.
     * 
     * @param id The department ID.
     * @param jefeId The user ID to be assigned as head.
     * @return [Response] with the modified [DepartamentoResponse].
     */
    @PATCH("api/v1/departamentos/{id}/asignar-jefe")
    suspend fun asignarJefe(
        @Path("id") id: Long, 
        @Query("jefeId") jefeId: Long
    ): Response<DepartamentoResponse>

    /**
     * Toggles the enabled state of a department.
     * 
     * @param request [ToggleStatusRequest] containing the IDs to flip.
     * @return [Response] with the updated [DepartamentoResponse].
     */
    @PATCH("api/v1/departamentos/estado")
    suspend fun toggleEstado(@Body request: ToggleStatusRequest): Response<DepartamentoResponse>

    /**
     * Creates a new organizational department.
     * 
     * @param request [CreateDepartmentRequest] with basic metadata.
     * @return [Response] with the created [DepartamentoResponse].
     */
    @POST("api/v1/departamentos")
    suspend fun crearDepartamento(@Body request: CreateDepartmentRequest): Response<DepartamentoResponse>

    /**
     * Updates structural department details.
     * 
     * @param request [UpdateDepartmentRequest] with changes.
     * @return [Response] with the updated [DepartamentoResponse].
     */
    @PUT("api/v1/departamentos")
    suspend fun actualizarDepartamento(@Body request: UpdateDepartmentRequest): Response<DepartamentoResponse>
}