package ls.diplomski.euterpe.ui.camerascreen

import android.util.Log
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
    private val mediaPlayerHelper: MediaPlayerHelper
) : ViewModel() {

    private val _uploadState = MutableStateFlow(UploadState())
    val uploadState: StateFlow<UploadState> = _uploadState.asStateFlow()

    fun uploadMusicSheet(imageFile: File) {
        viewModelScope.launch {
            repository.uploadAndProcessMusicSheet(imageFile)
                .collect { state ->
                    _uploadState.value = state

                    // Auto-play MIDI when upload is complete
                    if (state.isComplete && state.midiFile != null) {
                        playMidiFile(state.midiFile)
                    }
                }
        }
    }

    private fun playMidiFile(file: File) {
        mediaPlayerHelper.playMidi(file) {
            // Playback completed
            Log.d("CameraViewModel", "MIDI playback completed")
        }
    }

    override fun onCleared() {
        super.onCleared()
        mediaPlayerHelper.release()
    }
}
