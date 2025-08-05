package ls.diplomski.euterpe.ui.musicsnippetlist

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import ls.diplomski.euterpe.ui.musicsnippetlist.components.MusicSnippetListItem
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MusicSnippetListScreen(
    onElementClick: (String) -> Unit
) {
    val viewModel: MusicSnippetListScreenViewModel = koinViewModel()
    val viewState = viewModel.viewState.collectAsState().value
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_PAUSE || event == Lifecycle.Event.ON_STOP) {
                viewModel.stopPlayingSnippet()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
    Column {
        LaunchedEffect(Unit) {
            viewModel.reloadMusicalSnippets()
        }
        LazyColumn(
            contentPadding = PaddingValues(vertical = 24.dp),
        ) {
            items(viewState.items, key = { it.snippetName }) {
                MusicSnippetListItem(
                    viewState = it,
                    onDeleteClick = viewModel::deleteMidiFileAtPath,
                    onPlayStopButtonClick = viewModel::playSnippet,
                    onElementClick = { filePath ->
                        onElementClick(filePath)
                    }
                )
            }
        }
    }
}