package ls.diplomski.euterpe.domain.mappers

import ls.diplomski.euterpe.data.MusicSnippet
import ls.diplomski.euterpe.ui.musicsnippetlist.components.MusicSnippetListItemViewState

interface MusicSnippetToViewStateMapper {
    fun toMusicSnippetViewState(musicSnippet: MusicSnippet): MusicSnippetListItemViewState
}