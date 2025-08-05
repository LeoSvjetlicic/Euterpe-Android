package ls.diplomski.euterpe.ui.detailsscreen.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun PianoKeyboard(
    activeNotes: Set<Int> = emptySet(),
    startOctave: Int = 2,
    octaveCount: Int = 4,
    availableWidth: Dp,
    availableHeight: Dp
) {
    val totalWhiteKeys = octaveCount * 7
    val whiteKeyWidth = availableWidth / totalWhiteKeys
    val whiteKeyHeight =
        minOf(availableHeight * 0.7f, 140.dp)
    val blackKeyWidth = whiteKeyWidth * 0.6f
    val blackKeyHeight = whiteKeyHeight * 0.55f

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(whiteKeyHeight),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            repeat(octaveCount) { octave ->
                val octaveNumber = startOctave + octave
                DrawOctaveWhiteKeys(
                    octaveNumber = octaveNumber,
                    keyWidth = whiteKeyWidth,
                    keyHeight = whiteKeyHeight,
                    activeNotes = activeNotes
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
        ) {
            repeat(octaveCount) { octave ->
                val octaveNumber = startOctave + octave
                DrawOctaveBlackKeys(
                    octaveNumber = octaveNumber,
                    whiteKeyWidth = whiteKeyWidth,
                    blackKeyWidth = blackKeyWidth,
                    blackKeyHeight = blackKeyHeight,
                    activeNotes = activeNotes
                )
            }
        }
    }
}
