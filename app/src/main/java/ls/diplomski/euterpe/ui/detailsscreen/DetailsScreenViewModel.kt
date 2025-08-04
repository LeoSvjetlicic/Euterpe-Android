package ls.diplomski.euterpe.ui.detailsscreen

import android.util.Log
import androidx.lifecycle.ViewModel
import ls.diplomski.euterpe.utils.MediaPlayerHelper
import java.io.File

class DetailsScreenViewModel(
    private val mediaPlayerHelper: MediaPlayerHelper
) : ViewModel() {

    fun playMidiFile(filePath: String) {
        mediaPlayerHelper.playMidi(File(filePath)) {
            // Playback completed
            Log.d("CameraViewModel", "MIDI playback completed")
        }
    }

    fun stopPlayingSnippet() {
        mediaPlayerHelper.release()
    }
}