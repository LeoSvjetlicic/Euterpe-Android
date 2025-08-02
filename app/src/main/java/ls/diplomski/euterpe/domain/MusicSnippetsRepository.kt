package ls.diplomski.euterpe.domain

import ls.diplomski.euterpe.data.MusicSnippet

interface MusicSnippetsRepository {
    suspend fun fetchMusicSnippets(): List<MusicSnippet>
    suspend fun fetchMusicSnippetById(): MusicSnippet
}