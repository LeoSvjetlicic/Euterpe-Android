package ls.diplomski.euterpe.ui.musicsnippetlist.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.ExperimentalWearMaterialApi
import androidx.wear.compose.material.FractionalThreshold
import androidx.wear.compose.material.rememberSwipeableState
import androidx.wear.compose.material.swipeable
import kotlin.math.roundToInt

data class MusicSnippetListItemViewState(
    val id: String,
    val snippetName: String,
    val filePath: String,
    val isSnippetPlaying: Boolean,
    val dateCreated: String,
)

@OptIn(ExperimentalWearMaterialApi::class)
@Composable
fun MusicSnippetListItem(
    viewState: MusicSnippetListItemViewState,
    onDeleteClick: (String) -> Unit,
    onPlayStopButtonClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    onElementClick: (String) -> Unit,
) {
    val swipeableState = rememberSwipeableState(0)
    val sizePx = with(LocalDensity.current) { -40.dp.toPx() }
    val anchors = mapOf(0f to 0, sizePx to 1)
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        modifier = modifier
            .fillMaxWidth()
            .swipeable(
                state = swipeableState,
                anchors = anchors,
                thresholds = { _, _ -> FractionalThreshold(0.3f) },
                orientation = Orientation.Horizontal
            )
    ) {
        Icon(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 22.dp)
                .clip(RoundedCornerShape(10.dp))
                .clickable(interactionSource, null) { onDeleteClick(viewState.filePath) },
            imageVector = Icons.Filled.Delete,
            contentDescription = "Delete",
            tint = Red
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 6.dp)
                .clip(RoundedCornerShape(16.dp))
                .offset { IntOffset(swipeableState.offset.value.roundToInt(), 0) }
                .clickable {
                    onElementClick(viewState.filePath)
                },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors()
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = viewState.snippetName,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = viewState.dateCreated,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(Modifier.width(12.dp))

                PlayPauseButton(
                    modifier = Modifier,
                    isSnippetPlaying = viewState.isSnippetPlaying,
                    onClick = { onPlayStopButtonClick(viewState.id) }
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
            onDeleteClick = {},
            onPlayStopButtonClick = {}) {}
        MusicSnippetListItem(
            MusicSnippetListItemViewState(
                "",
                "My second song",
                "",
                true,
                "2.8.2025."
            ),
            onDeleteClick = {},
            onPlayStopButtonClick = {}) {}
    }
}