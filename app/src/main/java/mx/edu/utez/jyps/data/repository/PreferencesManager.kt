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
        /** Key alias mapping to the encrypted user roles (comma-separated or JSON) */
        val ROLES_KEY = stringPreferencesKey("user_roles")
        /** Key alias mapping to the encrypted user's full name */
        val NAME_KEY = stringPreferencesKey("user_name")
        /** Key alias mapping to the encrypted user's email address */
        val EMAIL_KEY = stringPreferencesKey("user_email")
        /** Key alias mapping to the encrypted user's phone number */
        val PHONE_KEY = stringPreferencesKey("user_phone")
        /** Key alias mapping to the encrypted user's paternal lastname */
        val PATERNAL_LASTNAME_KEY = stringPreferencesKey("user_paternal_last")
        /** Key alias mapping to the encrypted user's maternal lastname */
        val MATERNAL_LASTNAME_KEY = stringPreferencesKey("user_maternal_last")
        /** Key alias mapping to the encrypted user identifier */
        val USER_ID_KEY = stringPreferencesKey("user_id_long")
        /** Key alias mapping to the encrypted department name */
        val DEPT_NAME_KEY = stringPreferencesKey("dept_name")
        /** Key alias mapping to the encrypted department identifier */
        val DEPT_ID_KEY = stringPreferencesKey("dept_id_long")
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
     * Exposes a reactive Flow of the decrypted roles string.
     */
    val rolesFlow: Flow<String?> = context.dataStore.data
        .map { preferences ->
            val encryptedRoles = preferences[ROLES_KEY]
            if (encryptedRoles != null) {
                try {
                    val decoded = Base64.decode(encryptedRoles, Base64.DEFAULT)
                    val decrypted = aead.decrypt(decoded, null)
                    String(decrypted, Charsets.UTF_8)
                } catch (e: Exception) {
                    null
                }
            } else {
                null
            }
        }

    /**
     * Exposes a reactive Flow of the user's basic profile details.
     * Combines name, email and phone into a Triple for easy consumption.
     */
    val userProfileFlow: Flow<Triple<String, String, String>> = context.dataStore.data
        .map { preferences ->
            val encryptedName = preferences[NAME_KEY]
            val encryptedEmail = preferences[EMAIL_KEY]
            val encryptedPhone = preferences[PHONE_KEY]

            val name = encryptedName?.let {
                try {
                    val decoded = Base64.decode(it, Base64.DEFAULT)
                    val decrypted = aead.decrypt(decoded, null)
                    String(decrypted, Charsets.UTF_8)
                } catch (e: Exception) { "Usuario" }
            } ?: "Usuario"

            val email = encryptedEmail?.let {
                try {
                    val decoded = Base64.decode(it, Base64.DEFAULT)
                    val decrypted = aead.decrypt(decoded, null)
                    String(decrypted, Charsets.UTF_8)
                } catch (e: Exception) { "" }
            } ?: ""

            val phone = encryptedPhone?.let {
                try {
                    val decoded = Base64.decode(it, Base64.DEFAULT)
                    val decrypted = aead.decrypt(decoded, null)
                    String(decrypted, Charsets.UTF_8)
                } catch (e: Exception) { "No disponible" }
            } ?: "No disponible"

            Triple(name, email, phone)
        }

    /**
     * Exposes a reactive Flow of the decrypted user email.
     */
    val userEmailFlow: Flow<String?> = context.dataStore.data
        .map { preferences ->
            val encryptedEmail = preferences[EMAIL_KEY]
            if (encryptedEmail != null) {
                try {
                    val decoded = Base64.decode(encryptedEmail, Base64.DEFAULT)
                    val decrypted = aead.decrypt(decoded, null)
                    String(decrypted, Charsets.UTF_8)
                } catch (e: Exception) {
                    null
                }
            } else {
                null
            }
        }

    /**
     * Exposes a reactive Flow of the decrypted user full name.
     */
    val userNameFlow: Flow<String?> = context.dataStore.data
        .map { preferences ->
            val encryptedName = preferences[NAME_KEY]
            val encryptedPaternal = preferences[PATERNAL_LASTNAME_KEY]
            val encryptedMaternal = preferences[MATERNAL_LASTNAME_KEY]
            
            val name = encryptedName?.let { decrypt(it) } ?: "Usuario"
            val paternal = encryptedPaternal?.let { decrypt(it) } ?: ""
            val maternal = encryptedMaternal?.let { decrypt(it) } ?: ""
            
            // Build the full name based on available parts
            listOf(name, paternal, maternal)
                .filter { it.isNotBlank() }
                .joinToString(" ")
                .trim()
                .takeIf { it.isNotEmpty() }
        }

    /** Helper to decrypt a base64 encoded string from preferences */
    private fun decrypt(base64: String): String? {
        return try {
            val decoded = Base64.decode(base64, Base64.DEFAULT)
            val decrypted = aead.decrypt(decoded, null)
            String(decrypted, Charsets.UTF_8)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Synchronously resolves the current DataStore state for auth headers.
     * Required exclusively for OkHttp Interceptors which operate on blocking background threads.
     * 
     * @return The decrypted token or null if unresolvable or missing.
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
     * Atomically persists the complete session data (token, roles, and profile).
     * Utilizes Google Tink AEAD to ensure data is encrypted at rest within the DataStore.
     * 
     * @param token The identity JWT to be encrypted.
     * @param roles List of authorized scopes.
     * @param name User's display name.
     * @param email User's identity email.
     * @param phone Registered contact phone.
     */
    suspend fun saveSession(
        token: String,
        roles: List<String>,
        name: String,
        email: String,
        phone: String,
        paternal: String = "",
        maternal: String = "",
        userId: Long = 0,
        deptName: String = "",
        deptId: Long = 0
    ) {
        val encryptedToken = aead.encrypt(token.toByteArray(Charsets.UTF_8), null)
        val encryptedRoles = aead.encrypt(roles.joinToString(",").toByteArray(Charsets.UTF_8), null)
        val encryptedName = aead.encrypt(name.toByteArray(Charsets.UTF_8), null)
        val encryptedEmail = aead.encrypt(email.toByteArray(Charsets.UTF_8), null)
        val encryptedPhone = aead.encrypt(phone.toByteArray(Charsets.UTF_8), null)
        val encryptedPaternal = aead.encrypt(paternal.toByteArray(Charsets.UTF_8), null)
        val encryptedMaternal = aead.encrypt(maternal.toByteArray(Charsets.UTF_8), null)
        val encryptedUserId = aead.encrypt(userId.toString().toByteArray(Charsets.UTF_8), null)
        val encryptedDeptName = aead.encrypt(deptName.toByteArray(Charsets.UTF_8), null)
        val encryptedDeptId = aead.encrypt(deptId.toString().toByteArray(Charsets.UTF_8), null)

        context.dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = Base64.encodeToString(encryptedToken, Base64.DEFAULT)
            preferences[ROLES_KEY] = Base64.encodeToString(encryptedRoles, Base64.DEFAULT)
            preferences[NAME_KEY] = Base64.encodeToString(encryptedName, Base64.DEFAULT)
            preferences[EMAIL_KEY] = Base64.encodeToString(encryptedEmail, Base64.DEFAULT)
            preferences[PHONE_KEY] = Base64.encodeToString(encryptedPhone, Base64.DEFAULT)
            preferences[PATERNAL_LASTNAME_KEY] = Base64.encodeToString(encryptedPaternal, Base64.DEFAULT)
            preferences[MATERNAL_LASTNAME_KEY] = Base64.encodeToString(encryptedMaternal, Base64.DEFAULT)
            preferences[USER_ID_KEY] = Base64.encodeToString(encryptedUserId, Base64.DEFAULT)
            preferences[DEPT_NAME_KEY] = Base64.encodeToString(encryptedDeptName, Base64.DEFAULT)
            preferences[DEPT_ID_KEY] = Base64.encodeToString(encryptedDeptId, Base64.DEFAULT)
        }
    }

    /**
     * Purges the authentication token, roles, and profile data atomically.
     * Destroys all sensitive cryptographic materials stored in preferences.
     */
    suspend fun clearSession() {
        context.dataStore.edit { preferences ->
            preferences.remove(TOKEN_KEY)
            preferences.remove(ROLES_KEY)
            preferences.remove(NAME_KEY)
            preferences.remove(EMAIL_KEY)
            preferences.remove(PHONE_KEY)
            preferences.remove(PATERNAL_LASTNAME_KEY)
            preferences.remove(MATERNAL_LASTNAME_KEY)
            preferences.remove(USER_ID_KEY)
            preferences.remove(DEPT_NAME_KEY)
            preferences.remove(DEPT_ID_KEY)
        }
    }
}
