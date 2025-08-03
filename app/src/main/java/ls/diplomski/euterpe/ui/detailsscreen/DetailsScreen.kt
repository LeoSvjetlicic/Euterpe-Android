package ls.diplomski.euterpe.ui.detailsscreen

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import org.koin.androidx.compose.koinViewModel

@Composable
fun DetailsScreen(midiFilePath: String) {
    val viewModel: DetailsScreenViewModel = koinViewModel()
    Column {
        // Your details UI here...

        Button(
            onClick = {
                viewModel.playMidiFile(midiFilePath)
            }
        ) {
            Text("Play MIDI")
        }
    }
}
