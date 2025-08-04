package ls.diplomski.euterpe.ui.detailsscreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ls.diplomski.euterpe.utils.MediaPlayerHelper
import ls.diplomski.euterpe.utils.MidiParser
import java.io.File

class DetailsScreenViewModel(
    private val mediaPlayerHelper: MediaPlayerHelper,
    private val midiParser: MidiParser
) : ViewModel() {

    private val _uiState = MutableStateFlow(MidiAnalysisUiState())
    val uiState: StateFlow<MidiAnalysisUiState> = _uiState.asStateFlow()

    private var playbackJob: Job? = null

    fun analyzeMidiFile(filePath: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                val file = File(filePath)
                val result = midiParser.parseMidiFile(file)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    analysisResult = result
                )
            } catch (e: Exception) {
                Log.e("DetailsScreenViewModel", "Failed to analyze MIDI file", e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to analyze MIDI file: ${e.message}"
                )
            }
        }
    }

    fun playMidiFile(filePath: String) {
        _uiState.value = _uiState.value.copy(isPlaying = true, currentTimeMs = 0L)

        // Start playback tracking
        startPlaybackTracking()

        mediaPlayerHelper.playMidi(File(filePath)) {
            // Playback completed
            stopPlaybackTracking()
            _uiState.value = _uiState.value.copy(
                isPlaying = false,
                currentTimeMs = 0L,
                activeNotes = emptySet()
            )
            Log.d("DetailsScreenViewModel", "MIDI playback completed")
        }
    }

    fun stopPlayingSnippet() {
        mediaPlayerHelper.release()
        stopPlaybackTracking()
        _uiState.value = _uiState.value.copy(
            isPlaying = false,
            currentTimeMs = 0L,
            activeNotes = emptySet()
        )
    }

    private fun startPlaybackTracking() {
        playbackJob = viewModelScope.launch {
            val startTime = System.currentTimeMillis()

            while (_uiState.value.isPlaying) {
                val currentTime = System.currentTimeMillis() - startTime
                val activeNotes = getActiveNotesAtTime(currentTime)

                _uiState.value = _uiState.value.copy(
                    currentTimeMs = currentTime,
                    activeNotes = activeNotes
                )

                delay(50) // Update every 50ms for smooth animation
            }
        }
    }

    private fun stopPlaybackTracking() {
        playbackJob?.cancel()
        playbackJob = null
    }

    private fun getActiveNotesAtTime(timeMs: Long): Set<Int> {
        val analysisResult = _uiState.value.analysisResult ?: return emptySet()

        return analysisResult.notes
            .filter { note ->
                timeMs >= note.startTimeMs && timeMs <= note.endTimeMs
            }
            .map { it.noteNumber }
            .toSet()
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    override fun onCleared() {
        super.onCleared()
        stopPlaybackTracking()
    }
}
