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
    /**
     * Intercepts and modifies outgoing HTTP requests to include the active JWT.
     * Implements global security policies including automatic session clearing on 401 Unauthorized.
     * 
     * @param chain The [Interceptor.Chain] providing access to the current request.
     * @return [Response] resulting from the modified request chain.
     */
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
        // A 401 indicates authentication failure (invalid token), so we log out.
        // A 403 indicates authorization failure (lack of permission) and SHOULD NOT force a logout.
        if (response.code == 401) {
            runBlocking {
                prefs?.clearSession()
            }
        }

        return response
    }
}
