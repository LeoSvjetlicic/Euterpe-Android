package ls.diplomski.euterpe.utils

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import java.io.File

class MediaPlayerHelper(private val context: Context) {
    private var mediaPlayer: MediaPlayer? = null

    fun playMidi(file: File, onComplete: () -> Unit) {
        release()

        try {
            mediaPlayer = MediaPlayer().apply {
                setDataSource(file.absolutePath)
                prepare()
                setOnCompletionListener {
                    release()
                    onComplete()
                }
                start()
            }
        } catch (e: Exception) {
            Log.e("MediaPlayerHelper", "Error playing MIDI file", e)
            onComplete()
        }
    }

    fun release() {
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
