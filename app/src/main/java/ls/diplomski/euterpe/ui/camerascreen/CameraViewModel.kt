package ls.diplomski.euterpe.ui.camerascreen

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ls.diplomski.euterpe.domain.MusicSnippetRemoteRepository
import ls.diplomski.euterpe.utils.MediaPlayerHelper
import java.io.File

class CameraViewModel(
    private val repository: MusicSnippetRemoteRepository,
    private val mediaPlayerHelper: MediaPlayerHelper,
) : ViewModel() {

    private val _uploadState = MutableStateFlow(UploadState())
    val uploadState: StateFlow<UploadState> = _uploadState.asStateFlow()

    fun uploadMusicSheet(imageFile: File, onSuccessfulSave: (String) -> Unit) {
        viewModelScope.launch {
            repository.uploadAndProcessMusicSheet(imageFile)
                .collect { state ->
                    _uploadState.value = state

                    if (state.isComplete && state.midiFile != null) {
                        onSuccessfulSave(state.midiFile.path)
                    }
                }
        }
    }

    fun saveMidiPermanently(tempMidiFile: File, context: Context): File {
        val midiDir = File(context.getExternalFilesDir(null), "midi_files")
        if (!midiDir.exists()) midiDir.mkdirs()

        val permanentMidiFile = File(midiDir, "midi_${System.currentTimeMillis()}.mid")
        tempMidiFile.copyTo(permanentMidiFile, overwrite = true)
        return permanentMidiFile
    }

    override fun onCleared() {
        super.onCleared()
        mediaPlayerHelper.release()
    }
}
