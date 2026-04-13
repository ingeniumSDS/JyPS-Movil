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
import mx.edu.utez.jyps.data.model.PassResponse
import mx.edu.utez.jyps.data.model.PassRequest
import mx.edu.utez.jyps.data.model.JustificationResponse
import mx.edu.utez.jyps.data.model.ReviewPassRequest
import mx.edu.utez.jyps.data.model.ReviewJustificationRequest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Central interface defining all Retrofit HTTP operations.
 * Maps the mobile app requests directly to the REST backend endpoints.
 */
interface ApiService {

    // â”€â”€ AutenticaciÃ³n â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
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

    // â”€â”€ Usuarios â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
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

    // â”€â”€ Departamentos â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
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

    // â”€â”€ Check IN/OUT â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    /**
     * Officializes the exit or return of a personnel pass.
     *
     * @param qr The unique 6-character alphanumeric code extracted from the scanned pass.
     * @return [Response] wrapping the updated [PassResponse] indicating status transition.
     */
    @PATCH("api/v1/pases/{qr}")
    suspend fun processPassCheckout(@Path("qr") qr: String): Response<PassResponse>

    // â”€â”€ Pases de Salida â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    /**
     * Retrieves the complete list of exit passes associated with a specific employee.
     *
     * @param empleadoId The unique database identifier of the employee.
     * @return [List] of [PassResponse] objects.
     */
    @GET("api/v1/pases/empleado")
    suspend fun getPasesPorEmpleado(@Query("empleadoId") empleadoId: Long): List<PassResponse>

    /**
     * Retrieves the complete list of exit passes associated with a specific manager/department head.
     * 
     * @param jefeId The unique database identifier of the manager.
     * @return [List] of [PassResponse] objects.
     */
    @GET("api/v1/pases/jefe")
    suspend fun getPasesPorJefe(@Query("jefeId") jefeId: Long): List<PassResponse>

    /**
     * Allows a manager to review a pending exit pass, approving or rejecting it.
     * 
     * @param request [ReviewPassRequest] containing the review details.
     * @return [Response] wrapping the updated [PassResponse].
     */
    @PUT("api/v1/pases/revisar")
    suspend fun revisarPase(@Body request: ReviewPassRequest): Response<PassResponse>

    /**
     * Retrieves full granular details for a specific exit pass.
     *
     * @param id The primary key identifier of the pass.
     * @return [Response] wrapping the [PassResponse] data.
     */
    @GET("api/v1/pases/{id}/detalles")
    suspend fun getPaseDetalles(@Path("id") id: Long): Response<PassResponse>

    /**
     * Registers a new exit pass request with optional logic for attachments.
     * 
     * @param data Request body containing pass metadata encoded as application/json.
     * @param archivos Optional file parts (Ignored for standard passes per backend specs).
     * @return [Response] wrapping the created [PassResponse].
     */
    @Multipart
    @POST("api/v1/pases")
    suspend fun crearPase(
        @Part("data") data: RequestBody,
        @Part archivos: List<MultipartBody.Part>? = null
    ): Response<PassResponse>

    /**
     * Permanently removes a pending exit pass from the system.
     * 
     * @param id The primary key identifier of the pass.
     * @return [Response] with [ResponseBody] indicating success or network failure.
     */
    @DELETE("api/v1/pases/{id}")
    suspend fun eliminarPase(@Path("id") id: Long): Response<ResponseBody>

    // â”€â”€ Justificantes â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    /**
     * Retrieves all justifications associated with a specific employee.
     *
     * @param empleadoId The unique database identifier of the employee.
     * @return [List] of [JustificationResponse] objects.
     */
    @GET("api/v1/justificantes/empleado")
    suspend fun getJustificantesPorEmpleado(@Query("empleadoId") empleadoId: Long): List<JustificationResponse>

    /**
     * Retrieves the complete list of justification requests associated with a specific manager/department head.
     * 
     * @param jefeId The unique database identifier of the manager.
     * @return [List] of [JustificationResponse] objects.
     */
    @GET("api/v1/justificantes/jefe")
    suspend fun getJustificantesPorJefe(@Query("jefeId") jefeId: Long): List<JustificationResponse>

    /**
     * Allows a manager to review a pending justification, approving or rejecting it.
     * 
     * @param request [ReviewJustificationRequest] containing the review details.
     * @return [Response] wrapping the updated [JustificationResponse].
     */
    @PUT("api/v1/justificantes/revisar")
    suspend fun revisarJustificante(@Body request: ReviewJustificationRequest): Response<JustificationResponse>

    /**
     * Retrieves full granular details for a specific justification request.
     *
     * @param id The primary key identifier of the justification.
     * @return [Response] wrapping the [JustificationResponse] data.
     */
    @GET("api/v1/justificantes/{id}/detalles")
    suspend fun getJustificanteDetalles(@Path("id") id: Long): Response<JustificationResponse>

    /**
     * Downloads an attached file associated with a specific justification.
     *
     * @param empleadoId The employee ID who owns the file.
     * @param nombreArchivo The exact name of the file to retrieve.
     * @return [Response] with a [ResponseBody] representing the file content.
     */
    @GET("api/v1/justificantes/{empleadoId}/{nombreArchivo}")
    suspend fun descargarArchivoJustificante(
        @Path("empleadoId") empleadoId: Long,
        @Path("nombreArchivo") nombreArchivo: String
    ): Response<ResponseBody>

    /**
     * Registers a new justification request with optional file evidence.
     * Uses multipart encoding to handle binary data transmission.
     *
     * @param empleadoId The employee owner of the request.
     * @param fechaSolicitada The target date to justify (YYYY-MM-DD).
     * @param descripcion Detailed explanation of the incident.
     * @param archivos Optional list of files to be uploaded as evidence.
     * @return [Response] wrapping the created [JustificationResponse].
     */
    @Multipart
    @POST("api/v1/justificantes")
    suspend fun crearJustificante(
        @Part("data") data: RequestBody,
        @Part archivos: List<MultipartBody.Part>?
    ): Response<JustificationResponse>

    /**
     * Permanently removes a pending justification record from the system.
     */
    @DELETE("api/v1/justificantes/{id}")
    suspend fun eliminarJustificante(@Path("id") id: Long): Response<ResponseBody>
}
