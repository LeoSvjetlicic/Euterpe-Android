package ls.diplomski.euterpe.ui.detailsscreen.components

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import ls.diplomski.euterpe.ui.detailsscreen.MidiAnalysisResult
import java.io.File

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