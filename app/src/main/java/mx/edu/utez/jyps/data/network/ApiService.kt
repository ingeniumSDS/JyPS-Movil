package mx.edu.utez.jyps.data.network

import mx.edu.utez.jyps.data.model.Usuario
import mx.edu.utez.jyps.data.model.UserRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    @GET("api/v1/usuarios")
    suspend fun getUsuarios(): List<Usuario>

    @GET("api/v1/usuarios/{id}")
    suspend fun getUsuarioPorId(@Path("id") id: Long): Usuario

    @POST("api/v1/usuarios")
    suspend fun registrarUsuario(@Body request: UserRequest): Response<Void>
}