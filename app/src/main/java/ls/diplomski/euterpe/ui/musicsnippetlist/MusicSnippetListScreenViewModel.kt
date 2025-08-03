package ls.diplomski.euterpe.ui.musicsnippetlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ls.diplomski.euterpe.domain.FetchMusicSnippetsUseCase
import ls.diplomski.euterpe.utils.MediaPlayerHelper

class MusicSnippetListScreenViewModel(
    private val mediaPlayerHelper: MediaPlayerHelper,
    private val fetchMusicSnippetsUseCase: FetchMusicSnippetsUseCase
) : ViewModel() {
    private val _viewState = MutableStateFlow(MusicSnippetListScreenViewState(emptyList()))
    val viewState = _viewState.asStateFlow()

    init {
        viewModelScope.launch {
            val result = fetchMusicSnippetsUseCase()
//            TODO - check if it is success otherwise show error
            _viewState.emit(
                MusicSnippetListScreenViewState(
                    result
                )
            )
        }
    }

    fun playSnippet(id: String) {
        mediaPlayerHelper.release()
        setAllIsPlayingValues(id)
//        if (_viewState.value.items.find { it.isSnippetPlaying } != null) {
//            mediaPlayerHelper.playMidi() {

//                setAllIsPlayingValues()
//            }
//        }
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