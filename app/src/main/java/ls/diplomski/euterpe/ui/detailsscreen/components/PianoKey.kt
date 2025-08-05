package ls.diplomski.euterpe.ui.detailsscreen.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ls.diplomski.euterpe.ui.detailsscreen.getNoteNameFromMidi

@Composable
fun PianoKey(
    modifier: Modifier = Modifier,
    isBlack: Boolean,
    isPressed: Boolean,
    noteNumber: Int
) {
    val keyColor = when {
        isPressed && isBlack -> Color(0xFF4CAF50) // Green when pressed
        isPressed && !isBlack -> Color(0xFF81C784) // Light green when pressed
        isBlack -> Color(0xFF212121) // Dark gray for black keys
        else -> Color.White // White for white keys
    }

    val borderColor = if (isBlack) Color.Transparent else Color(0xFF999999)

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(0f, 0f, 50f, 50f),
        colors = CardDefaults.cardColors(containerColor = keyColor),
        border = if (!isBlack) BorderStroke(0.1.dp, borderColor) else BorderStroke(
            0.1.dp,
            Color.Black
        ),
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
        ) {
            if (!isBlack) {
                Text(
                    textAlign = TextAlign.Center,
                    text = getNoteNameFromMidi(noteNumber),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isPressed) Color.White else Color.Black,
                    modifier = Modifier.padding(4.dp)
                )
            }
        }
    }
}
