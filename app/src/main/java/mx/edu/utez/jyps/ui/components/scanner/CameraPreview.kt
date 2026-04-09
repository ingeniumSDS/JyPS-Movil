package mx.edu.utez.jyps.ui.components.scanner

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview as CameraXPreview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import mx.edu.utez.jyps.utils.QRCodeAnalyzer
import java.util.concurrent.Executors

/**
 * Composable that integrates a live CameraX [PreviewView] with QR code analysis.
 *
 * Handles the CAMERA permission lifecycle internally:
 * 1. If granted → starts the camera.
 * 2. If denied → shows a rationale dialog, then guides the user to Settings.
 *
 * The camera preview is rendered at square 1:1 aspect ratio, cropped from the center of the
 * sensor to match the existing [ScannerBox] frame.
 *
 * @param onQrDetected Callback fired with the decoded QR string (debounced by [QRCodeAnalyzer]).
 * @param onFrameWithQr Callback fired each frame: true when a QR is visible, false otherwise.
 * @param modifier Modifier applied to the root Box.
 */
@Composable
fun CameraPreview(
    onQrDetected: (String) -> Unit,
    onFrameWithQr: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var hasCameraPermission by remember { mutableStateOf(false) }
    var permissionDeniedPermanently by remember { mutableStateOf(false) }
    var permissionChecked by remember { mutableStateOf(false) }

    // Launcher for the system permission dialog
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
        permissionDeniedPermanently = !isGranted
        permissionChecked = true
    }

    // Check current permission state on first composition
    LaunchedEffect(Unit) {
        val permission = android.Manifest.permission.CAMERA
        val granted = ContextCompat.checkSelfPermission(
            context, permission
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED

        if (granted) {
            hasCameraPermission = true
            permissionChecked = true
        } else {
            // Request permission — the rationale is shown system-side
            permissionLauncher.launch(permission)
        }
    }

    Box(modifier = modifier) {
        when {
            hasCameraPermission -> {
                // Live camera preview in a square crop
                AndroidView(
                    factory = { ctx ->
                        val previewView = PreviewView(ctx).apply {
                            // FILL_CENTER scales and crops the sensor output to fill the square
                            scaleType = PreviewView.ScaleType.FILL_CENTER
                        }

                        val executor = Executors.newSingleThreadExecutor()
                        val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

                        cameraProviderFuture.addListener({
                            val cameraProvider = cameraProviderFuture.get()

                            val preview = CameraXPreview.Builder().build().also {
                                it.setSurfaceProvider(previewView.surfaceProvider)
                            }

                            val imageAnalysis = ImageAnalysis.Builder()
                                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                                .build()
                                .also {
                                    it.setAnalyzer(
                                        executor,
                                        QRCodeAnalyzer(
                                            onQrDetected = onQrDetected,
                                            onFrameWithQr = onFrameWithQr
                                        )
                                    )
                                }

                            try {
                                cameraProvider.unbindAll()
                                cameraProvider.bindToLifecycle(
                                    lifecycleOwner,
                                    CameraSelector.DEFAULT_BACK_CAMERA,
                                    preview,
                                    imageAnalysis
                                )
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }, ContextCompat.getMainExecutor(ctx))

                        previewView
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }

            permissionChecked && permissionDeniedPermanently -> {
                // Permanent denial — guide user to Settings
                CameraPermissionDeniedView(
                    onOpenSettings = {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            data = Uri.fromParts("package", context.packageName, null)
                        }
                        context.startActivity(intent)
                    }
                )
            }

            permissionChecked -> {
                // Temporarily denied — minimal feedback
                CameraPermissionDeniedView(onOpenSettings = {
                    permissionLauncher.launch(android.Manifest.permission.CAMERA)
                })
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CameraPreviewPreview() {
    CameraPreview(
        onQrDetected = {},
        onFrameWithQr = {}
    )
}
