package ls.diplomski.euterpe.data.impl

import io.ktor.client.HttpClient
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.statement.HttpResponse
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import ls.diplomski.euterpe.domain.api.MusicSnippetsApiService

class MusicSnippetsApiServiceImpl(
    private val httpClient: HttpClient
) : MusicSnippetsApiService {

    override suspend fun uploadMusicSheet(imageBytes: ByteArray): HttpResponse {
        return httpClient.submitFormWithBinaryData(
            url = "https://your-api-endpoint.com/upload-sheet",
            formData = formData {
                append("file", imageBytes, Headers.build {
                    append(HttpHeaders.ContentType, "image/jpeg")
                    append(HttpHeaders.ContentDisposition, "filename=music_sheet.jpg")
                })
            }
        )
    }
}
