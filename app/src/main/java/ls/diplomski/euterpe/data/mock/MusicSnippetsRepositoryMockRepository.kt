package ls.diplomski.euterpe.data.mock

import ls.diplomski.euterpe.data.MusicSnippet
import ls.diplomski.euterpe.domain.MusicSnippetsRepository
import java.time.LocalDate

class MusicSnippetsRepositoryMockRepository : MusicSnippetsRepository {
    override suspend fun fetchMusicSnippets(): List<MusicSnippet> = listOf(
        MusicSnippet(
            id = "1",
            pathToMIDIFIle = "R.raw.converted123.mid",
            name = "MOCK music snippet",
            dateCreated = LocalDate.of(2025, 8, 2)
        ),
        MusicSnippet(
            id = "2",
            pathToMIDIFIle = "R.raw.converted123.mid",
            name = "MOCK music snippet",
            dateCreated = LocalDate.of(2025, 8, 2)
        ),
        MusicSnippet(
            id = "3",
            pathToMIDIFIle = "R.raw.converted123.mid",
            name = "MOCK music snippet",
            dateCreated = LocalDate.of(2025, 8, 2)
        ),
        MusicSnippet(
            id = "4",
            pathToMIDIFIle = "R.raw.converted123.mid",
            name = "MOCK music snippet",
            dateCreated = LocalDate.of(2025, 8, 2)
        )
    )

    override suspend fun fetchMusicSnippetById(): MusicSnippet = MusicSnippet(
        id = "1",
        pathToMIDIFIle = "R.raw.converted123.mid",
        name = "MOCK music snippet",
        dateCreated = LocalDate.of(2025, 8, 2)
    )
}