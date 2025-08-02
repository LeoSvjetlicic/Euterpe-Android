package ls.diplomski.euterpe.domain

import ls.diplomski.euterpe.domain.mappers.MusicSnippetToViewStateMapper

class FetchMusicSnippetsUseCase(
    private val musicSnippetsRepository: MusicSnippetsRepository,
    private val mapper: MusicSnippetToViewStateMapper
) {
    suspend operator fun invoke() =
        musicSnippetsRepository.fetchMusicSnippets().map { mapper.toMusicSnippetViewState(it) }
}