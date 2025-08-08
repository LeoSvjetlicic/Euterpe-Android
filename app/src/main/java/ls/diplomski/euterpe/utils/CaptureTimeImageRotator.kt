import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.view.OrientationEventListener
import java.io.File
import java.io.FileOutputStream

class CaptureTimeImageRotator(private val context: Context) {

    private var captureOrientation = 0
    private var isListening = false

    private val orientationListener = object : OrientationEventListener(context) {
        override fun onOrientationChanged(orientation: Int) {
            if (orientation != ORIENTATION_UNKNOWN) {
                captureOrientation = when {
                    orientation >= 315 || orientation < 45 -> 0
                    orientation >= 45 && orientation < 135 -> 90
                    orientation >= 135 && orientation < 225 -> 180
                    orientation >= 225 && orientation < 315 -> 270
                    else -> 0
                }
            }
        }
    }

    fun startListening() {
        if (!isListening && orientationListener.canDetectOrientation()) {
            orientationListener.enable()
            isListening = true
        }
    }

    fun captureCurrentOrientation(): Int {
        return captureOrientation
    }

    fun stopListening() {
        if (isListening) {
            orientationListener.disable()
            isListening = false
        }
    }

    fun rotateImageForCapturedOrientation(uri: Uri, capturedOrientation: Int): File {
        val inputStream = context.contentResolver.openInputStream(uri)
            ?: throw IllegalArgumentException("Cannot open input stream for: $uri")

        val originalBitmap = BitmapFactory.decodeStream(inputStream)
        inputStream.close()

        val rotationDegrees = when (capturedOrientation) {
            0 -> 0f      // Portrait
            90 -> -90f   // Landscape left
            180 -> 180f  // Upside down
            270 -> 90f   // Landscape right
            else -> 0f
        }

        val rotatedBitmap = if (rotationDegrees != 0f) {
            val matrix = Matrix().apply { postRotate(rotationDegrees) }
            Bitmap.createBitmap(
                originalBitmap, 0, 0,
                originalBitmap.width, originalBitmap.height,
                matrix, true
            )
        } else originalBitmap

        val outFile = File(context.cacheDir, "captured_rotated_${System.currentTimeMillis()}.jpg")
        FileOutputStream(outFile).use { out ->
            rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
        }

        if (rotatedBitmap != originalBitmap) {
            originalBitmap.recycle()
        }

        return outFile
    }
}
