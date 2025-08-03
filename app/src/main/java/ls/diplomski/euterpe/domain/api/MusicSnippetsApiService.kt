package ls.diplomski.euterpe.domain.api

import io.ktor.client.statement.HttpResponse

interface MusicSnippetsApiService {
    suspend fun uploadMusicSheet(imageBytes: ByteArray): HttpResponse
}
