package mx.edu.utez.jyps.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import timber.log.Timber

/**
 * Persists file metadata (technicalName -> localPath) using EncryptedSharedPreferences.
 * This ensures that sensitive documents mappings are stored securely.
 */
class FileMetadataStore(context: Context) {

    private val sharedPreferences: SharedPreferences by lazy {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        EncryptedSharedPreferences.create(
            context,
            "secure_file_metadata",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    /**
     * Saves the mapping between a server file and its local URI path.
     */
    fun saveFileMapping(technicalName: String, localPath: String) {
        Timber.d("Saving secure mapping: $technicalName -> $localPath")
        sharedPreferences.edit().putString(technicalName, localPath).apply()
    }

    /**
     * Retrieves all saved file mappings.
     */
    fun getAllMappings(): Map<String, String> {
        return sharedPreferences.all.mapValues { it.value.toString() }
    }

    /**
     * Clears all mappings. Should be called on logout.
     */
    fun clearAll() {
        Timber.i("Clearing all secure file mappings")
        sharedPreferences.edit().clear().apply()
    }
}
