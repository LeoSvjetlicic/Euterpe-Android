package ls.diplomski.euterpe.data.mappers

import ls.diplomski.euterpe.data.MusicSnippet
import ls.diplomski.euterpe.domain.mappers.MusicSnippetToViewStateMapper
import ls.diplomski.euterpe.ui.musicsnippetlist.components.MusicSnippetListItemViewState
import ls.diplomski.euterpe.utils.LocalDateTimeFormatter

class MusicSnippetToViewStateMapperImpl : MusicSnippetToViewStateMapper {
    override fun toMusicSnippetViewState(musicSnippet: MusicSnippet): MusicSnippetListItemViewState =
        MusicSnippetListItemViewState(
            id = musicSnippet.id,
            snippetName = musicSnippet.name,
            isSnippetPlaying = false,
            dateCreated = LocalDateTimeFormatter.toFormattedString(musicSnippet.dateCreated),
            filePath = musicSnippet.filePath
        )

}