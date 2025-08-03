package ls.diplomski.euterpe.ui.camerascreen

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import kotlinx.io.IOException
import ls.diplomski.euterpe.ui.DETAILS_SCREEN_BASE_ROUTE
import org.koin.androidx.compose.koinViewModel
import java.io.File

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraScreen(
    navController: NavController
) {
    val viewModel: CameraViewModel = koinViewModel()
    val uploadState by viewModel.uploadState.collectAsState()

    val cameraPermissionState = rememberPermissionState(
        Manifest.permission.CAMERA
    )

    when {
        cameraPermissionState.status.isGranted -> {
            CameraContent(
                uploadState = uploadState,
                cameraViewModel = viewModel,
                navController = navController
            )
        }

        cameraPermissionState.status.shouldShowRationale -> {
            PermissionRationale(
                onRequestPermission = {
                    cameraPermissionState.launchPermissionRequest()
                }
            )
        }

        else -> {
            LaunchedEffect(Unit) {
                cameraPermissionState.launchPermissionRequest()
            }
        }
    }
}

fun copyUriToFile(context: Context, uri: Uri, fileName: String): File {
    val inputStream = context.contentResolver.openInputStream(uri)
        ?: throw IllegalArgumentException("Unable to open input stream from URI")

    val tempFile = File(context.cacheDir, fileName)
    tempFile.outputStream().use { outputStream ->
        inputStream.copyTo(outputStream)
    }
    inputStream.close()
    return tempFile
}

fun fixImageOrientationAndSaveAsFile(context: Context, inputFile: File): File {
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


@Composable
fun CameraContent(
    uploadState: UploadState,
    cameraViewModel: CameraViewModel,
    navController: NavController
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cropLauncher = rememberLauncherForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful && result.uriContent != null) {
            val croppedUri = result.uriContent!!

            // Copy the cropped URI to a temp file
            val croppedFile = copyUriToFile(
                context,
                croppedUri,
                "cropped_sheet_${System.currentTimeMillis()}.png"
            )

            // Apply EXIF rotation correction and get the final rotated file
            val rotatedFile = fixImageOrientationAndSaveAsFile(context, croppedFile)

            // Upload the properly oriented file
            cameraViewModel.uploadMusicSheet(rotatedFile, onSuccessfulSave = { midiPath ->
                val permanentMidiFile = cameraViewModel.saveMidiPermanently(File(midiPath), context)
                val encodedPath = Uri.encode(permanentMidiFile.absolutePath)
                navController.navigate("$DETAILS_SCREEN_BASE_ROUTE/$encodedPath")
            })

        } else {
            Log.e("Crop", "Crop failed: ${result.error}")
        }
    }


    var imageCapture: ImageCapture? by remember { mutableStateOf(null) }
    val previewView = remember { PreviewView(context) }

    // Initialize camera
    LaunchedEffect(Unit) {
        val cameraProvider = ProcessCameraProvider.getInstance(context).get()

        val preview = Preview.Builder().build().also {
            it.surfaceProvider = previewView.surfaceProvider
        }

        imageCapture = ImageCapture.Builder().build()

        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                imageCapture
            )
        } catch (exc: Exception) {
            Log.e("CameraScreen", "Camera initialization failed", exc)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { previewView },
            modifier = Modifier.fillMaxSize()
        )

        if (uploadState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.7f)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    CircularProgressIndicator(color = Color.White)
                    Text(
                        "Processing music sheet...",
                        color = Color.White,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }

        if (uploadState.isComplete) {
            LaunchedEffect(uploadState.isComplete) {
                Toast.makeText(context, "MIDI file created and playing!", Toast.LENGTH_LONG).show()
            }
        }

        uploadState.error?.let { error ->
            LaunchedEffect(error) {
                Toast.makeText(context, "Error: $error", Toast.LENGTH_LONG).show()
            }
        }

        if (!uploadState.isLoading) {
            FloatingActionButton(
                onClick = {
                    // *** Capture image and start crop flow ***
                    imageCapture?.let { capture ->
                        captureImage(capture, context, cropLauncher)
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Take Photo"
                )
            }
        }
    }
}

// *** Modified capture to return Uri and start uCrop ***
private fun captureImage(
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

// *** Start uCrop activity ***
private fun startCrop(
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


// *** Fix orientation & scale image ***
//fun fixImageOrientationAndSize(context: Context, uri: Uri): File {
//    val inputStream = context.contentResolver.openInputStream(uri)
//    val bitmap = BitmapFactory.decodeStream(inputStream)
//    inputStream?.close()
//
//    if (bitmap == null) return File(uri.path ?: "")
//
//    val rotatedBitmap = if (bitmap.height > bitmap.width) {
//        val matrix = Matrix().apply { postRotate(90f) }
//        Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
//    } else bitmap
//
//    val fixedFile = File(context.cacheDir, "fixed_${System.currentTimeMillis()}.png")
//    FileOutputStream(fixedFile).use { out ->
//        rotatedBitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
//    }
//
//    return fixedFile
//}

@Composable
fun PermissionRationale(onRequestPermission: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Camera permission is needed to capture music sheets",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Button(onClick = onRequestPermission) {
            Text("Grant Permission")
        }
    }
}
