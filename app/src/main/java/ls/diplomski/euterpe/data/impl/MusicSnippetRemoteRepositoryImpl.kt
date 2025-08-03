package ls.diplomski.euterpe.data.impl

import android.content.Context
import io.ktor.client.statement.bodyAsBytes
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import ls.diplomski.euterpe.domain.MusicSnippetRemoteRepository
import ls.diplomski.euterpe.domain.api.MusicSnippetsApiService
import ls.diplomski.euterpe.ui.camerascreen.UploadState
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MusicSnippetRemoteRepositoryImpl(
    private val apiService: MusicSnippetsApiService,
    private val context: Context
) : MusicSnippetRemoteRepository {
    override suspend fun uploadAndProcessMusicSheet(imageFile: File): Flow<UploadState> =
        channelFlow {
            try {
                send(UploadState(isLoading = true))

                val imageBytes = imageFile.readBytes()
                val response = apiService.uploadMusicSheet(imageBytes)

                if (response.status.value in 200..299) {
                    // Generate unique filename with timestamp
                    val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)
                        .format(Date())
                    val fileName = "midi_${System.currentTimeMillis()}_$timestamp.mid"

                    // Save MIDI file to internal storage
                    val midiDir = File(context.getExternalFilesDir(null), "midi")
                    if (!midiDir.exists()) {
                        midiDir.mkdirs()
                    }

                    val midiFile = File(midiDir, fileName)
                    response.bodyAsBytes().let { midiBytes ->
                        FileOutputStream(midiFile).use { outputStream ->
                            outputStream.write(midiBytes)
                        }
                    }

                    send(UploadState(isLoading = false, isComplete = true, midiFile = midiFile))

                } else {
                    send(UploadState(error = "Upload failed: ${response.status.description}"))
                }
            } catch (e: Exception) {
                send(UploadState(error = e.message ?: "Unknown error occurred"))
            }
        }
}
