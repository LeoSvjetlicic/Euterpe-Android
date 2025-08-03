package ls.diplomski.euterpe.ui.camerascreen

import java.io.File

data class MidiUploadResponse(
    val id: String,
    val message: String
)

data class UploadState(
    val isLoading: Boolean = false,
    val isComplete: Boolean = false,
    val progress: Float = 0f,
    val error: String? = null,
    val midiFile: File? = null
)

