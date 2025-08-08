package ls.diplomski.euterpe.ui.detailsscreen

import android.annotation.SuppressLint
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import ls.diplomski.euterpe.R
import ls.diplomski.euterpe.ui.detailsscreen.components.MidiAnalysisContent
import ls.diplomski.euterpe.ui.detailsscreen.components.PianoKeyboard
import org.koin.androidx.compose.koinViewModel

@SuppressLint("DefaultLocale", "UnusedBoxWithConstraintsScope")
@Composable
fun DetailsScreen(
    midiFilePath: String
) {
    val viewModel: DetailsScreenViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current
    val orientation = LocalConfiguration.current.orientation
    var localMidiFilePath by rememberSaveable { mutableStateOf(midiFilePath) }

    LaunchedEffect(localMidiFilePath) {
        viewModel.analyzeMidiFile(localMidiFilePath)
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_PAUSE || event == Lifecycle.Event.ON_STOP) {
                viewModel.stopPlayingSnippet()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Column(
        modifier = Modifier
    ) {
        Card(
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = {
                        if (uiState.isPlaying) {
                            viewModel.stopPlayingSnippet()
                        } else {
                            viewModel.playMidiFile(localMidiFilePath)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        painter = painterResource(
                            if (uiState.isPlaying) R.drawable.ic_stop else R.drawable.ic_play
                        ),
                        contentDescription = if (uiState.isPlaying) "Stop" else "Play"
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (uiState.isPlaying) "Stop MIDI" else "Play MIDI")
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Piano Keyboard",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                BoxWithConstraints(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    PianoKeyboard(
                        activeNotes = uiState.activeNotes,
                        startOctave = 2,
                        octaveCount = 5,
                        availableWidth = maxWidth,
                        availableHeight = maxHeight
                    )
                }

                val notesToDisplay = if (uiState.activeNotes.isNotEmpty()) {
                    "Playing: ${uiState.activeNotes.joinToString { getNoteNameFromMidi(it) }}"
                } else {
                    " "
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = notesToDisplay,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            if (orientation == Orientation.Vertical.ordinal + 1) {
                when {
                    uiState.isLoading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator()
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Analyzing MIDI file...")
                            }
                        }
                    }

                    uiState.error != null -> {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color.Red.copy(alpha = 0.1f))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Analysis Error",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.Red
                                )
                                uiState.error?.let {
                                    Text(text = it)
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                TextButton(onClick = { viewModel.clearError() }) {
                                    Text("Dismiss")
                                }
                            }
                        }
                    }

                    uiState.analysisResult != null -> {
                        MidiAnalysisContent(uiState.analysisResult!!, localMidiFilePath) {
                            localMidiFilePath = it
                        }
                    }
                }
            }
        }
    }
}


fun getNoteNameFromMidi(midiNote: Int): String {
    val noteNames = arrayOf("C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B")
    val octave = (midiNote / 12) - 1
    val note = noteNames[midiNote % 12]
    return "$note$octave"
}
