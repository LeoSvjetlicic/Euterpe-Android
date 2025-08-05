package ls.diplomski.euterpe.ui.detailsscreen.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun DrawOctaveBlackKeys(
    octaveNumber: Int,
    whiteKeyWidth: Dp,
    blackKeyWidth: Dp,
    blackKeyHeight: Dp,
    activeNotes: Set<Int>
) {
    val blackKeyPositions = listOf(
        Pair(0.5f, 1), // C# between C and D
        Pair(1.5f, 3), // D# between D and E
        Pair(3.5f, 6), // F# between F and G
        Pair(4.5f, 8), // G# between G and A
        Pair(5.5f, 10) // A# between A and B
    )

    var currentPosition = 0f

    blackKeyPositions.forEach { (position, noteOffset) ->
        val midiNote = (octaveNumber + 1) * 12 + noteOffset
        val isActive = activeNotes.contains(midiNote)

        val spacerWidth = whiteKeyWidth * (position - currentPosition)
        if (spacerWidth > 0.dp) {
            Spacer(
                modifier = Modifier
                    .width(spacerWidth)
                    .height(5.dp)
            )
        }

        Box(
            modifier = Modifier
                .width(blackKeyWidth)
                .offset(x = (blackKeyWidth + 0.1.dp) / 2)
        ) {
            PianoKey(
                modifier = Modifier
                    .width(blackKeyWidth)
                    .height(blackKeyHeight)
                    .align(Alignment.Center),
                isBlack = true,
                isPressed = isActive,
                noteNumber = midiNote
            )
        }

        currentPosition = position + (blackKeyWidth / whiteKeyWidth)
    }

    val remainingSpace = whiteKeyWidth * (7f - currentPosition)
    if (remainingSpace > 0.dp) {
        Spacer(modifier = Modifier.width(remainingSpace))
    }
}