package ls.diplomski.euterpe.ui.musicsnippetlist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import ls.diplomski.euterpe.ui.musicsnippetlist.components.MusicSnippetListItem
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MusicSnippetListScreen(
    viewModel: MusicSnippetListScreenViewModel = koinViewModel(),
    onElementClick: (String) -> Unit
) {
    val viewState = viewModel.viewState.collectAsState().value
    Column {
        LazyColumn(
            contentPadding = PaddingValues(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(viewState.items) {
                MusicSnippetListItem(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable {
                            onElementClick(it.id)
                        },
                    viewState = it,
                    onPlayStopButtonClick = viewModel::playSnippet
                )
            }
        }
    }
}