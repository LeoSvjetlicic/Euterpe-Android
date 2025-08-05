package ls.diplomski.euterpe.ui.detailsscreen.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp

@Composable
fun RowScope.DrawOctaveWhiteKeys(
    octaveNumber: Int,
    keyWidth: Dp,
    keyHeight: Dp,
    activeNotes: Set<Int>
) {
    val whiteNoteOffsets = listOf(0, 2, 4, 5, 7, 9, 11) // C, D, E, F, G, A, B

    whiteNoteOffsets.forEach { noteOffset ->
        val midiNote = (octaveNumber + 1) * 12 + noteOffset
        val isActive = activeNotes.contains(midiNote)

        PianoKey(
            modifier = Modifier
                .width(keyWidth)
                .height(keyHeight)
                .weight(1f),
            isBlack = false,
            isPressed = isActive,
            noteNumber = midiNote
        )
    }
}