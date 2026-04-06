package mx.edu.utez.jyps.utils

import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.zxing.BinaryBitmap
import com.google.zxing.MultiFormatReader
import com.google.zxing.PlanarYUVLuminanceSource
import com.google.zxing.common.HybridBinarizer
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.atomic.AtomicReference

/**
 * CameraX [ImageAnalysis.Analyzer] implementation that decodes QR codes from live camera frames
 * using the ZXing library.
 *
 * A debounce mechanism prevents the same code from being reported more than once every
 * [debounceMs] milliseconds, avoiding duplicate triggers on consecutive frames.
 *
 * @param debounceMs Minimum milliseconds between two successful reads of the same code.
 * @param onQrDetected Called with the decoded string when a new QR code is found.
 * @param onFrameWithQr Called with true/false each frame to toggle the "Escaneando..." overlay.
 */
class QRCodeAnalyzer(
    private val debounceMs: Long = 1500L,
    private val onQrDetected: (String) -> Unit,
    private val onFrameWithQr: (Boolean) -> Unit
) : ImageAnalysis.Analyzer {

    private val reader = MultiFormatReader()
    private val lastDecodeTime = AtomicLong(0L)
    private val lastCode = AtomicReference<String>("")
    private val isProcessing = AtomicBoolean(false)

    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        // Avoid processing if another frame is still being analyzed
        if (isProcessing.getAndSet(true)) {
            imageProxy.close()
            return
        }

        val mediaImage = imageProxy.image
        if (mediaImage == null) {
            isProcessing.set(false)
            imageProxy.close()
            return
        }

        try {
            val buffer = mediaImage.planes[0].buffer
            val bytes = ByteArray(buffer.remaining())
            buffer.get(bytes)

            val source = PlanarYUVLuminanceSource(
                bytes,
                imageProxy.width,
                imageProxy.height,
                0, 0,
                imageProxy.width,
                imageProxy.height,
                false
            )

            val binaryBitmap = BinaryBitmap(HybridBinarizer(source))
            val result = reader.decodeWithState(binaryBitmap)

            // A QR was found in this frame — show the scanning overlay
            onFrameWithQr(true)

            val now = System.currentTimeMillis()
            val sameCode = result.text == lastCode.get()
            val withinDebounce = (now - lastDecodeTime.get()) < debounceMs

            // Only report if it is a new code or enough time has passed
            if (!sameCode || !withinDebounce) {
                lastCode.set(result.text)
                lastDecodeTime.set(now)
                onQrDetected(result.text)
            }

        } catch (e: Exception) {
            // No QR detected in this frame — hide the scanning overlay
            onFrameWithQr(false)
            reader.reset()
        } finally {
            isProcessing.set(false)
            imageProxy.close()
        }
    }
}
