package ls.diplomski.euterpe.ui.musicsnippetlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ls.diplomski.euterpe.domain.FetchMusicSnippetsUseCase
import ls.diplomski.euterpe.utils.MediaPlayerHelper
import java.io.File

class MusicSnippetListScreenViewModel(
    private val mediaPlayerHelper: MediaPlayerHelper,
    private val fetchMusicSnippetsUseCase: FetchMusicSnippetsUseCase
) : ViewModel() {
    private val _viewState = MutableStateFlow(MusicSnippetListScreenViewState(emptyList()))
    val viewState = _viewState.asStateFlow()

    fun reloadMusicalSnippets() {
        viewModelScope.launch {
            val result = fetchMusicSnippetsUseCase()
            _viewState.emit(
                MusicSnippetListScreenViewState(
                    result
                )
            )
        }
    }

    fun deleteMidiFileAtPath(filePath: String) {
        val file = File(filePath)
        if (file.exists() && file.isFile) {
            file.delete()
            mediaPlayerHelper.release()
            reloadMusicalSnippets()
        }
    }

    fun playSnippet(id: String) {
        mediaPlayerHelper.release()
        setAllIsPlayingValues(id)
        val selectedElement = _viewState.value.items.find { it.isSnippetPlaying }
        if (selectedElement != null) {
            mediaPlayerHelper.playMidi(File(selectedElement.filePath)) {
                setAllIsPlayingValues()
            }
        }
    }

    private fun setAllIsPlayingValues(id: String = "-1") {
        _viewState.update {
            it.copy(items = it.items.map { musicSnippet ->
                musicSnippet.copy(
                    isSnippetPlaying = musicSnippet.id == id && !musicSnippet.isSnippetPlaying
                )
            })
        }
    }
}