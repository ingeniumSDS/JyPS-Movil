package mx.edu.utez.jyps.data.network

import mx.edu.utez.jyps.data.model.User
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

import mx.edu.utez.jyps.data.model.UserRequest
import mx.edu.utez.jyps.data.model.UserWithDetails
import retrofit2.Response

interface ApiService { 

    @POST("api/v1/usuarios")
    suspend fun registrarUsuario(@Body request: UserRequest): Response<Void>

    @GET("api/v1/usuarios")
    suspend fun getUsuarios(): List<UserWithDetails>

    @GET("api/v1/usuarios/{id}")
    suspend fun getUsuarioPorId(@Path("id") id: Int): UserWithDetails

    @Multipart
    @POST("users")
    suspend fun addUser(
        @Part("nombre") nombre: RequestBody,
        @Part("descripcion") descripcion: RequestBody,
        @Part("numPases") numPases: RequestBody,
        @Part("fechaPase") fechaPase: RequestBody,
        @Part imagen: MultipartBody.Part? // El servidor espera 'imagen', no 'image'
    ): User

    @Multipart
    @PUT("users/{id}")
    suspend fun updateUser(
        @Path("id") id: Int,
        @Part("nombre") nombre: RequestBody,
        @Part("descripcion") descripcion: RequestBody,
        @Part("numPases") numPases: RequestBody,
        @Part("fechaPase") fechaPase: RequestBody,
        @Part imagen: MultipartBody.Part?
    ): User

    @DELETE("users/{id}")
    suspend fun deleteUser(@Path("id") id: Int)
}