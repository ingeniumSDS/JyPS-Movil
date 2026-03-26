package mx.edu.utez.jyps.data.network

import kotlinx.coroutines.runBlocking
import mx.edu.utez.jyps.data.repository.PreferencesManager
import okhttp3.Interceptor
import okhttp3.Response

/**
 * OkHttp Interceptor responsible for injecting the active session token into API requests
 * and handling session invalidation globally.
 * 
 * Provides a highly scalable mechanism ensuring all authenticated endpoints automatically
 * receive the JWT without manually passing tokens in every repository network call.
 * 
 * @param prefsProvider A supplier function resolving the `PreferencesManager` lazily. 
 * Prevents cyclic dependencies during the initialization of the OkHttpClient singleton.
 */
class AuthInterceptor(private val prefsProvider: () -> PreferencesManager?) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // Bypasses token injection for the login route to prevent sending expired/invalid
        // tokens which might lead to unnecessary 401 rejection on a public auth endpoint.
        if (originalRequest.url.encodedPath.contains("/login")) {
            return chain.proceed(originalRequest)
        }

        val prefs = prefsProvider()
        val token = prefs?.getTokenSync()

        val requestBuilder = originalRequest.newBuilder()
        if (!token.isNullOrEmpty()) {
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }

        val request = requestBuilder.build()
        val response = chain.proceed(request)

        // Intercepts unauthorized state (e.g., expired token, revoked session).
        // By clearing the DataStore here, the reactive 'isLoggedIn' Flow instantly emits false,
        // triggering a global logout UI navigation independently of where the API call originated.
        if (response.code == 401 || response.code == 403) {
            runBlocking {
                prefs?.clearSession()
            }
        }

        return response
    }
}
