package mx.edu.utez.jyps.utils

import com.google.firebase.crashlytics.FirebaseCrashlytics
import timber.log.Timber

/**
 * Centralized Crashlytics instrumentation facade for the JyPS application.
 *
 * All diagnostic telemetry flows through this single object, allowing
 * the entire instrumentation layer to be disabled with a single flag
 * flip before a production release.
 *
 * ## How to disable for production:
 * Set [ENABLED] to `false`. Every public method becomes a no-op.
 * Alternatively, remove this file and all call-sites will simply
 * need to be deleted (they are always guarded behind the flag).
 */
object CrashlyticsHelper {

    // ╔══════════════════════════════════════════════════════════╗
    // ║  KILL SWITCH — Set to false for production releases     ║
    // ╚══════════════════════════════════════════════════════════╝
    private const val ENABLED = true

    private val instance: FirebaseCrashlytics?
        get() = if (ENABLED) FirebaseCrashlytics.getInstance() else null

    // ─── User Identity ──────────────────────────────────────────

    /**
     * Tags the current Crashlytics session with the authenticated user's
     * identity so that crash reports can be filtered per-user in the console.
     *
     * Call once after a successful login.
     *
     * @param userId Primary key from the backend.
     * @param role   Primary role string (e.g. "EMPLEADO", "GUARDIA").
     * @param deptId Department the user belongs to.
     * @param email  Institutional email.
     */
    fun setUserContext(userId: Long, role: String, deptId: Long, email: String) {
        instance?.apply {
            setUserId(userId.toString())
            setCustomKey("role", role)
            setCustomKey("dept_id", deptId)
            setCustomKey("email", email)
            log("Session started: userId=$userId, role=$role, deptId=$deptId")
            Timber.d("Crashlytics: User context set — id=$userId role=$role dept=$deptId")
        }
    }

    /**
     * Clears user identity after logout.
     */
    fun clearUserContext() {
        instance?.apply {
            setUserId("")
            setCustomKey("role", "")
            setCustomKey("dept_id", 0L)
            setCustomKey("email", "")
            log("Session cleared")
        }
    }

    // ─── Screen Navigation Breadcrumbs ──────────────────────────

    /**
     * Logs a navigation event so that crash reports show the user's
     * journey through the app prior to the incident.
     *
     * @param screen Human-readable screen name (e.g. "PassRequestScreen").
     */
    fun logScreenView(screen: String) {
        instance?.apply {
            setCustomKey("last_screen", screen)
            log("Screen: $screen")
        }
    }

    // ─── Critical Action Breadcrumbs ────────────────────────────

    /**
     * Records a user-initiated action that may be relevant for debugging.
     *
     * @param screen Originating screen.
     * @param action Short description (e.g. "submit_pass", "approve_justification").
     * @param extras Optional key-value metadata.
     */
    fun logAction(screen: String, action: String, extras: Map<String, String> = emptyMap()) {
        instance?.apply {
            val meta = if (extras.isNotEmpty()) extras.entries.joinToString { "${it.key}=${it.value}" } else ""
            log("[$screen] $action $meta".trim())
        }
    }

    // ─── API Call Instrumentation ────────────────────────────────

    /**
     * Logs the initiation of an API request. Paired with [logApiSuccess]
     * or [logApiError] to form a complete request lifecycle trace.
     *
     * @param method HTTP method (GET, POST, PUT, PATCH, DELETE).
     * @param endpoint The URL path (e.g. "/api/v1/pases").
     */
    fun logApiCall(method: String, endpoint: String) {
        instance?.log("API → $method $endpoint")
    }

    /**
     * Logs a successful API response.
     *
     * @param method HTTP method.
     * @param endpoint The URL path.
     * @param statusCode HTTP status (e.g. 200).
     */
    fun logApiSuccess(method: String, endpoint: String, statusCode: Int = 200) {
        instance?.log("API ✓ $method $endpoint ($statusCode)")
    }

    /**
     * Logs a failed API response and records the exception as a **non-fatal**
     * issue in the Crashlytics console.
     *
     * @param method HTTP method.
     * @param endpoint The URL path.
     * @param error The caught exception or throwable.
     * @param statusCode HTTP status code if available.
     */
    fun logApiError(method: String, endpoint: String, error: Throwable, statusCode: Int? = null) {
        instance?.apply {
            val code = statusCode?.let { " ($it)" } ?: ""
            log("API ✗ $method $endpoint$code — ${error.message}")
            setCustomKey("last_failed_api", "$method $endpoint")
            recordException(error)
        }
    }

    // ─── Generic Non-Fatal Exception Recording ──────────────────

    /**
     * Records any non-fatal exception so it appears under "Non-fatals"
     * in the Firebase console. Use this for caught errors that don't
     * crash the app but indicate something unexpected.
     *
     * @param context Brief description of what was happening.
     * @param error The throwable to record.
     */
    fun recordNonFatal(context: String, error: Throwable) {
        instance?.apply {
            log("Non-fatal: $context — ${error.message}")
            recordException(error)
        }
    }
}
