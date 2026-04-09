package mx.edu.utez.jyps.data.repository

import android.content.Context
import android.util.Base64
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.crypto.tink.Aead
import com.google.crypto.tink.KeyTemplates
import com.google.crypto.tink.aead.AeadConfig
import com.google.crypto.tink.integration.android.AndroidKeysetManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

/**
 * Application-level DataStore instance for authentication settings.
 * Utilizes `preferencesDataStore` to ensure a single source of truth and avoid multi-threading conflicts.
 */
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_settings")

/**
 * Manages secure storage of primitive data types using Jetpack DataStore paired with Google Tink AEAD.
 * 
 * Provides an asynchronous `Flow` for reactive UI updates and synchronous fallback methods 
 * required by blocking interceptors (e.g., OkHttp AuthInterceptor).
 */
class PreferencesManager(private val context: Context) {

    private val aead: Aead

    init {
        // Initializes Google Tink registry for AEAD primitives
        AeadConfig.register()

        // Builds the KeysetManager anchored to the Android Hardware Keystore.
        // On fresh installs or after a factory reset the tink_master_key won't exist yet.
        // In that case we wipe the stale SharedPrefs keyset (which references a now-missing key)
        // so the Builder can generate a brand-new key on the next attempt.
        aead = try {
            buildKeysetManager()
        } catch (e: Exception) {
            // Clear the orphaned keyset entry that points to the deleted Keystore key
            context.getSharedPreferences("secure_prefs", Context.MODE_PRIVATE)
                .edit()
                .remove("tink_keyset")
                .apply()
            // Also clear any tokens encrypted with the old key — they are unrecoverable
            runBlocking {
                context.dataStore.edit { it.clear() }
            }
            // Retry with a clean slate; this will generate a new hardware-backed key
            buildKeysetManager()
        }
    }

    /**
     * Builds and returns the Tink [Aead] primitive backed by the Android Hardware Keystore.
     * Extracted to avoid duplication in the init recovery block.
     */
    private fun buildKeysetManager(): Aead =
        AndroidKeysetManager.Builder()
            .withSharedPref(context, "tink_keyset", "secure_prefs")
            .withKeyTemplate(KeyTemplates.get("AES256_GCM"))
            .withMasterKeyUri("android-keystore://tink_master_key")
            .build()
            .keysetHandle
            .getPrimitive(Aead::class.java)

    companion object {
        /** Key alias mapping to the encrypted JWT token payload */
        val TOKEN_KEY = stringPreferencesKey("jwt_token")
    }

    /**
     * Exposes a reactive Flow of the decrypted token.
     * Decryption occurs lazily as emissions arrive. If decryption fails due to
     * corrupted keys or tampered Keystore vectors, it safely falls back to null.
     */
    val tokenFlow: Flow<String?> = context.dataStore.data
        .map { preferences ->
            val encryptedToken = preferences[TOKEN_KEY]
            if (encryptedToken != null) {
                try {
                    val decodedToken = Base64.decode(encryptedToken, Base64.DEFAULT)
                    val decryptedToken = aead.decrypt(decodedToken, null)
                    String(decryptedToken, Charsets.UTF_8)
                } catch (e: Exception) {
                    null // Safe boundary: treat corrupted tokens as unauthenticated
                }
            } else {
                null
            }
        }

    /**
     * Synchronously resolves the current DataStore state.
     * Required exclusively for OkHttp Interceptors which operate on blocking background threads.
     * 
     * @return The decrypted token or null if unresolvable.
     */
    fun getTokenSync(): String? {
        val encryptedToken = runBlocking { context.dataStore.data.first()[TOKEN_KEY] } ?: return null
        return try {
            val decodedToken = Base64.decode(encryptedToken, Base64.DEFAULT)
            val decryptedToken = aead.decrypt(decodedToken, null)
            String(decryptedToken, Charsets.UTF_8)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Encrypts and persists the token into the DataStore preferences.
     * Converts to Base64 post-encryption to adhere to schema primitive constraints.
     */
    suspend fun saveToken(token: String) {
        val encryptedToken = aead.encrypt(token.toByteArray(Charsets.UTF_8), null)
        val encodedToken = Base64.encodeToString(encryptedToken, Base64.DEFAULT)
        context.dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = encodedToken
        }
    }

    /**
     * Purges the authentication token. Used during explicit logouts or unauthorized interceptions.
     */
    suspend fun clearSession() {
        context.dataStore.edit { preferences ->
            preferences.remove(TOKEN_KEY)
        }
    }
}
