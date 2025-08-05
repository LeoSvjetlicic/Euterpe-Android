package ls.diplomski.euterpe.ui.detailsscreen

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import ls.diplomski.euterpe.R
import org.koin.androidx.compose.koinViewModel
import java.io.File

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

    // Auto-analyze the MIDI file when screen loads
    LaunchedEffect(localMidiFilePath) {
        viewModel.analyzeMidiFile(localMidiFilePath)
    }

    // Handle lifecycle events
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
                        MidiAnalysisContent(uiState.analysisResult!!, midiFilePath) {
                            localMidiFilePath = it
                        }
                    }
                }
            }
        }
    }
}

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

@SuppressLint("DefaultLocale")
@Composable
fun MidiAnalysisContent(
    result: MidiAnalysisResult,
    filePath: String,
    onFileRename: (String) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            val originalFile = File(filePath)
            val initialName = remember { mutableStateOf(originalFile.nameWithoutExtension) }
            val isEditing = remember { mutableStateOf(false) }
            val newName = remember { mutableStateOf(initialName.value) }

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        if (isEditing.value) {
                            androidx.compose.material3.TextField(
                                colors = TextFieldDefaults.colors(
                                    unfocusedIndicatorColor = Color.Transparent,
                                    disabledIndicatorColor = Color.Transparent,
                                    focusedIndicatorColor = Color.Transparent,
                                    errorIndicatorColor = Color.Transparent,
                                ),
                                value = newName.value,
                                onValueChange = { newName.value = it },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                            TextButton(onClick = {
                                originalFile.parentFile?.let { parent ->
                                    if (initialName.value != newName.value) {
                                        val newFile = File(parent, "${newName.value}.mid")
                                        val success = originalFile.renameTo(newFile)
                                        if (success) {
                                            initialName.value = newName.value
                                            isEditing.value = false
                                            onFileRename(newFile.path)
                                        }
                                    } else {
                                        isEditing.value = false
                                    }
                                }
                            }) {
                                Text("Save")
                            }
                        } else {
                            Text(
                                text = initialName.value,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f),
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                            Icon(
                                imageVector = Icons.Default.Create,
                                contentDescription = "Edit",
                                modifier = Modifier
                                    .padding(start = 8.dp)
                                    .clickable { isEditing.value = true }
                            )
                        }
                    }
                    HorizontalDivider(Modifier.padding(vertical = 8.dp))

                    Text(
                        text = "MIDI Analysis Summary",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Total Notes: ${result.notes.size}")
                    Text(
                        "Duration: ${
                            String.format(
                                "%.2f",
                                result.totalDurationMs / 1000.0
                            )
                        } seconds"
                    )

                    if (result.tempoChanges.isNotEmpty()) {
                        Text(
                            "Current BPM: ${
                                String.format(
                                    "%.1f",
                                    result.tempoChanges.last().bpm
                                )
                            }"
                        )
                    }
                }
            }
        }

        item {
            Text(
                text = "Notes (${result.notes.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        items(result.notes.take(50)) { note ->
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = note.getNoteName(),
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Velocity: ${note.velocity}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Channel: ${note.channel}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "${String.format("%.2f", note.startTimeMs / 1000.0)}s",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "Duration: ${note.duration}ms",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        if (result.notes.size > 50) {
            item {
                Text(
                    text = "... and ${result.notes.size - 50} more notes",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

private fun getNoteNameFromMidi(midiNote: Int): String {
    val noteNames = arrayOf("C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B")
    val octave = (midiNote / 12) - 1
    val note = noteNames[midiNote % 12]
    return "$note$octave"
}
