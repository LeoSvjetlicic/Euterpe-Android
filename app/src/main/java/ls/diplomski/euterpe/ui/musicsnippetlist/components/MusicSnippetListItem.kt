package ls.diplomski.euterpe.ui.musicsnippetlist.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class MusicSnippetListItemViewState(
    val id: String,
    val snippetName: String,
    val filePath: String,
    val isSnippetPlaying: Boolean,
    val dateCreated: String,
)

@Composable
fun MusicSnippetListItem(
    viewState: MusicSnippetListItemViewState,
    modifier: Modifier = Modifier,
    onPlayStopButtonClick: (String) -> Unit
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 12.dp
        )
    ) {
        Row(Modifier.padding(12.dp)) {
            Text(
                text = viewState.snippetName, modifier = Modifier.weight(1f),
                fontSize = 24.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.width(8.dp))
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                PlayPauseButton(
                    modifier = Modifier.size(24.dp),
                    isSnippetPlaying = viewState.isSnippetPlaying,
                    onClick = { onPlayStopButtonClick(viewState.id) }
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    text = viewState.dateCreated,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Preview
@Composable
private fun MusicSnippetListItemPreview() {
    Column {
        MusicSnippetListItem(
            MusicSnippetListItemViewState(
                "",
                "My first song with a very long namea afj apfja spfaf a fafa",
                "",
                false,
                "2.8.2025."
            ),
            onPlayStopButtonClick = {})
        MusicSnippetListItem(
            MusicSnippetListItemViewState(
                "",
                "My second song",
                "",
                true,
                "2.8.2025."
            ),
            onPlayStopButtonClick = {})
    }
}