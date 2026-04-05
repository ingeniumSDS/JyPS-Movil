package mx.edu.utez.jyps.data.network

import android.content.Context
import mx.edu.utez.jyps.data.repository.PreferencesManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Singleton factory for providing configured Retrofit and OkHttp instances.
 * 
 * Centralizes the network configuration, ensuring that all remote API calls share
 * a single connection pool, logging behavior, and standard headers.
 */
object RetrofitInstance {

    // Actualiza esta IP según tu configuración de red
    private const val BASE_URL = "https://yolonda-otocystic-personably.ngrok-free.dev/"

    private var preferencesManager: PreferencesManager? = null

    /**
     * Initializes the component with application context.
     * Required specifically to bootstrap the [PreferencesManager] dependency
     * before any authenticated API calls are executed.
     */
    fun init(context: Context) {
        if (preferencesManager == null) {
            preferencesManager = PreferencesManager(context.applicationContext)
        }
    }

    /**
     * Inspects and logs payload body in internal network layers for debuggability.
     * Uses a custom logger to redact sensitive attributes (like passwords) from the 
     * Logcat console, preventing CWE-532 vulnerabilities.
     */
    private val loggingInterceptor = HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger {
        override fun log(message: String) {
            // Redact sensitive JSON keys in bodies
            var redactedMessage = message.replace(
                Regex("\"(password|tokenJwt)\":\"[^\"]*\""),
                "\"$1\":\"***REDACTED***\""
            )
            
            // Redact Authorization headers (e.g., "Authorization: Bearer <token>")
            redactedMessage = redactedMessage.replace(
                Regex("(?i)Authorization: Bearer [^\\s]+"),
                "Authorization: Bearer ***REDACTED***"
            )

            android.util.Log.i("OkHttp", redactedMessage)
        }
    }).apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    /**
     * Lazy bridge to the [AuthInterceptor] ensuring the [PreferencesManager] is 
     * successfully instantiated through [init] prior to intercepting HTTP requests.
     */
    private val authInterceptor = AuthInterceptor { preferencesManager }

    /**
     * Core OkHttp client. Defines explicit timeouts to prevent thread hanging on 
     * slow connections, improving UX responsiveness.
     */
    private val okHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    /**
     * Builder configuring Retrofit mapped with Gson converters aligning with the backend schemas.
     */
    private val retrofit by lazy {
        android.util.Log.d("RetrofitInstance", "Creando instancia de Retrofit con URL: $BASE_URL")
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create()) // Usa Gson
            .build()
    }

    /**
     * Exposes the single API routing interface for the whole app.
     */
    val api: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}