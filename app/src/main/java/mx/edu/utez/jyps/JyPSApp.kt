package mx.edu.utez.jyps

import android.app.Application
import timber.log.Timber

/**
 * Main application class for JyPS.
 * Initializes core libraries and global configurations.
 */
class JyPSApp : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Plant a DebugTree to enable Timber logs in the Logcat
        // This is required for Timber.d(), Timber.e(), etc. to be visible
        Timber.plant(Timber.DebugTree())
    }
}
