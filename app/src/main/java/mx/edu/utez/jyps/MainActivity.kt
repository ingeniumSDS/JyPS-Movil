package mx.edu.utez.jyps

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import com.google.firebase.appdistribution.FirebaseAppDistribution
import com.google.firebase.appdistribution.InterruptionLevel
import mx.edu.utez.jyps.data.network.RetrofitInstance
import mx.edu.utez.jyps.ui.NavigationHost
import mx.edu.utez.jyps.ui.theme.JyPSTheme

/**
 * Main entry point of the JyPS application.
 * Bootstraps the dependency injection, sets up Edge-to-Edge display, and initializes the App Navigation.
 * Also requests POST_NOTIFICATIONS permission (Android 13+) required for the
 * Firebase App Distribution in-app feedback notification.
 */
class MainActivity : ComponentActivity() {

    /**
     * Launcher that handles the POST_NOTIFICATIONS runtime permission result.
     * Once granted, shows the Firebase App Distribution feedback notification.
     */
    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                showFeedbackNotification()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        RetrofitInstance.init(this)

        // Request POST_NOTIFICATIONS on Android 13+; on older versions show directly.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                this, Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            if (granted) {
                showFeedbackNotification()
            } else {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            showFeedbackNotification()
        }

        enableEdgeToEdge()
        setContent {
            JyPSTheme {
                NavigationHost()
            }
        }
    }

    /**
     * Displays the Firebase App Distribution persistent feedback notification.
     * No-op in release builds where the full App Distribution SDK is absent.
     */
    private fun showFeedbackNotification() {
        FirebaseAppDistribution.getInstance().showFeedbackNotification(
            R.string.feedback_notice,
            InterruptionLevel.HIGH
        )
    }
}

/**
 * Preview for the main activity structure.
 */
@Preview(showBackground = true)
@Composable
fun MainActivityPreview() {
    JyPSTheme {
        NavigationHost()
    }
}