package ls.diplomski.euterpe.domain

import kotlinx.coroutines.flow.Flow
import ls.diplomski.euterpe.ui.camerascreen.UploadState
import java.io.File

interface MusicSnippetRemoteRepository {
    suspend fun uploadAndProcessMusicSheet(imageFile: File): Flow<UploadState>
}