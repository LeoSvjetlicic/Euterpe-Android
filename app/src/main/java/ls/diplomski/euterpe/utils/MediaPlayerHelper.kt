package ls.diplomski.euterpe.utils

import android.content.Context
import android.media.MediaPlayer

class MediaPlayerHelper(
    private val context: Context
) {
    private var mediaPlayer: MediaPlayer? = null

    fun playMidi(resourceId: Int, onComplete: () -> Unit) {
        release()  // Release any previous player

        mediaPlayer = MediaPlayer.create(context, resourceId)
        mediaPlayer?.apply {
            setOnCompletionListener {
                release()
                onComplete() // <- Call the callback
            }
            start()
        }
    }

    fun release() {
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
