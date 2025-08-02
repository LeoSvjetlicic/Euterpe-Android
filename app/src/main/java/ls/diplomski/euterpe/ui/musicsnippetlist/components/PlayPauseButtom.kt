package ls.diplomski.euterpe.ui.musicsnippetlist.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Green
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import ls.diplomski.euterpe.R

@Composable
fun PlayPauseButton(
    isSnippetPlaying: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val mutableInteractionSource = remember { MutableInteractionSource() }
    val (icon, iconColor, iconDescription) = if (isSnippetPlaying) {
        Triple(ImageVector.Companion.vectorResource(R.drawable.ic_stop), Red, "play button")
    } else {
        Triple(
            ImageVector.Companion.vectorResource(R.drawable.ic_play),
            Green, "play button"
        )
    }
    Icon(
        modifier = modifier.clickable(mutableInteractionSource, null) {
            onClick()
        },
        imageVector = icon,
        tint = iconColor,
        contentDescription = iconDescription,
    )
}