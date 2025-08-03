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
            url = "http://172.24.249.129:3000/convert",
            formData = formData {
                append("image", imageBytes, Headers.build {
                    append(HttpHeaders.ContentType, "image/png")
                    append(HttpHeaders.ContentDisposition, "filename=music_sheet.png")
                })
            }
        )
    }
}
