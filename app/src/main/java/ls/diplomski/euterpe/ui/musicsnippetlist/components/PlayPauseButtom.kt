package ls.diplomski.euterpe.ui.musicsnippetlist.components

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import ls.diplomski.euterpe.R

@Composable
fun PlayPauseButton(
    isSnippetPlaying: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val mutableInteractionSource = remember { MutableInteractionSource() }
    val isDarkTheme = isSystemInDarkTheme()

    val animatedColor by animateColorAsState(
        targetValue = if (isSnippetPlaying) Color.Red else if (isDarkTheme) {
            Color.Green
        } else {
            Color(0xFF2cd431)
        },
        animationSpec = tween(
            durationMillis = 300,
            easing = FastOutSlowInEasing
        ),
        label = "button_color"
    )

    val pressed by mutableInteractionSource.collectIsPressedAsState()
    val animatedScale by animateFloatAsState(
        targetValue = if (pressed) 0.9f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "button_scale"
    )
    Box(
        modifier = modifier
            .scale(animatedScale)
            .clickable(
                interactionSource = mutableInteractionSource,
                indication = null
            ) {
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {

        Crossfade(
            targetState = isSnippetPlaying,
            animationSpec = tween(200),
            label = "icon_crossfade"
        ) { isPlaying ->
            Icon(
                imageVector = if (isPlaying) {
                    ImageVector.vectorResource(R.drawable.ic_stop)
                } else {
                    ImageVector.vectorResource(R.drawable.ic_play)
                },
                tint = animatedColor,
                contentDescription = if (isPlaying) "stop button" else "play button",
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
