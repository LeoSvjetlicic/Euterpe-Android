package ls.diplomski.euterpe.ui.camerascreen

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.exifinterface.media.ExifInterface
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import kotlinx.io.IOException
import java.io.File

internal fun copyUriToFile(context: Context, uri: Uri, fileName: String): File {
    val inputStream = context.contentResolver.openInputStream(uri)
        ?: throw IllegalArgumentException("Unable to open input stream from URI")

    val tempFile = File(context.cacheDir, fileName)
    tempFile.outputStream().use { outputStream ->
        inputStream.copyTo(outputStream)
    }
    inputStream.close()
    return tempFile
}

internal fun fixImageOrientationAndSaveAsFile(context: Context, inputFile: File): File {
    // Load bitmap from the input file
    val bitmap = BitmapFactory.decodeFile(inputFile.absolutePath) ?: return inputFile

    // Read EXIF orientation info
    val exif = try {
        ExifInterface(inputFile.absolutePath)
    } catch (e: IOException) {
        e.printStackTrace()
        return inputFile // Return original file if EXIF reading fails
    }

    val orientation = exif.getAttributeInt(
        ExifInterface.TAG_ORIENTATION,
        ExifInterface.ORIENTATION_NORMAL
    )

    // Determine rotation degrees
    val rotationDegrees = when (orientation) {
        ExifInterface.ORIENTATION_ROTATE_90 -> 90f
        ExifInterface.ORIENTATION_ROTATE_180 -> 180f
        ExifInterface.ORIENTATION_ROTATE_270 -> 270f
        else -> 0f
    }

    // Create rotated bitmap if rotation is needed
    val rotatedBitmap = if (rotationDegrees != 0f) {
        val matrix = Matrix().apply { postRotate(rotationDegrees) }
        Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    } else {
        bitmap
    }

    // Save the rotated bitmap to a new file
    val rotatedFile = File(context.cacheDir, "rotated_${System.currentTimeMillis()}.png")
    rotatedFile.outputStream().use { out ->
        rotatedBitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
    }

    // Clean up bitmaps to avoid memory leaks
    if (rotatedBitmap != bitmap) {
        bitmap.recycle()
    }

    return rotatedFile
}

internal fun captureImage(
    imageCapture: ImageCapture,
    context: Context,
    cropLauncher: ActivityResultLauncher<CropImageContractOptions>
) {
    val photoFile = File(context.externalCacheDir, "sheet_${System.currentTimeMillis()}.png")
    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

    imageCapture.takePicture(
        outputOptions,
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                val uri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.provider",
                    photoFile
                )
                startCrop(uri, cropLauncher)
            }

            override fun onError(exception: ImageCaptureException) {
                Log.e("Camera", "Capture failed", exception)
            }
        }
    )
}

internal fun startCrop(
    uri: Uri,
    cropLauncher: ActivityResultLauncher<CropImageContractOptions>,
) {
    cropLauncher.launch(
        CropImageContractOptions(
            uri = uri,
            cropImageOptions = CropImageOptions(
                guidelines = CropImageView.Guidelines.ON,
                outputCompressFormat = Bitmap.CompressFormat.PNG,
                showCropOverlay = true
            )
        )
    )
}
