package mx.edu.utez.jyps.data.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitInstance { // De momento solo sirve de referencia

    // ¡¡¡MUY IMPORTANTE!!!
    // Esta es la IP que usa el emulador de Android para conectarse
    // al servidor SpringBoot en tu computadora.
    // Actualiza esta IP según tu configuración de red
    private const val BASE_URL = "https://yolonda-otocystic-personably.ngrok-free.dev/"

    // Interceptor para logging de peticiones HTTP
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // Cliente OkHttp con logging
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    // Creación "perezosa" (lazy) de la instancia de Retrofit
    private val retrofit by lazy {
        android.util.Log.d("RetrofitInstance", "Creando instancia de Retrofit con URL: $BASE_URL")
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create()) // Usa Gson
            .build()
    }

    // Instancia pública de tu ApiService
    val api: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}