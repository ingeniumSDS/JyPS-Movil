package mx.edu.utez.jyps.data.network

import okhttp3.ResponseBody
import org.json.JSONObject
import timber.log.Timber

/**
 * Utility class to parse complex error responses from the backend.
 * Handles single 'mensaje' strings and nested 'detalles' maps for field validation.
 */
object NetworkErrorParser {

    /**
     * Parses a ResponseBody into a human-readable error message.
     * Extracts the main 'mensaje' and appends 'detalles' if present.
     */
    fun parseError(errorBody: ResponseBody?): String {
        val jsonString = errorBody?.string() ?: return "Error de comunicación con el servidor"
        
        return try {
            val json = JSONObject(jsonString)
            val mainMessage = json.optString("mensaje", "Error en el servidor")
            val details = json.optJSONObject("detalles")
            
            if (details != null && details.length() > 0) {
                // If there are specific field details, format them nicely
                val detailList = mutableListOf<String>()
                val keys = details.keys()
                while (keys.hasNext()) {
                    val key = keys.next()
                    val value = details.getString(key)
                    detailList.add(value)
                }
                // Return main message + the first specific detail found (usually the most relevant)
                "$mainMessage: ${detailList.joinToString(". ")}"
            } else {
                mainMessage
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to parse error JSON: $jsonString")
            "Error inesperado del servidor"
        }
    }
}
