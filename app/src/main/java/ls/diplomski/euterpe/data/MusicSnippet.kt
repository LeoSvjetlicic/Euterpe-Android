package ls.diplomski.euterpe.data

import java.time.LocalDate

data class MusicSnippet(
    val id: String,
    val filePath: String,
    val name: String,
    val dateCreated: LocalDate,
)
