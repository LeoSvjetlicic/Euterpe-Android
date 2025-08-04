package ls.diplomski.euterpe.utils

import dev.atsushieno.ktmidi.MidiMusic
import dev.atsushieno.ktmidi.read
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ls.diplomski.euterpe.ui.detailsscreen.MidiAnalysisResult
import ls.diplomski.euterpe.ui.detailsscreen.MidiMetaType
import ls.diplomski.euterpe.ui.detailsscreen.MidiNoteEvent
import ls.diplomski.euterpe.ui.detailsscreen.TempoChange
import java.io.File

class MidiParser {

    suspend fun parseMidiFile(file: File): MidiAnalysisResult = withContext(Dispatchers.IO) {
        val midiMusic = MidiMusic()
        midiMusic.read(file.readBytes().toList())

        val ticksPerQuarter = midiMusic.deltaTimeSpec
        val noteEvents = mutableListOf<MidiNoteEvent>()
        val tempoChanges = mutableListOf<TempoChange>()

        // Track active notes (noteNumber -> (startTick, velocity, channel))
        val activeNotes = mutableMapOf<Int, Triple<Long, Int, Int>>()

        // Default tempo (120 BPM = 500,000 microseconds per quarter note)
        var currentTempo = 500_000
        tempoChanges.add(TempoChange(0, currentTempo))

        // Process all tracks
        for (track in midiMusic.tracks) {
            var absoluteTick = 0L

            for (message in track.messages) {
                absoluteTick += message.deltaTime

                val event = message.event

                when {
                    // Tempo change (meta event)
                    event.eventType.toUnsigned() == MidiMusic.META_EVENT &&
                            event.metaType.toUnsigned() == MidiMetaType.TEMPO -> {
                        val data = event.extraData
                        if (data != null && data.size >= event.extraDataOffset + 3) {
                            currentTempo = MidiMusic.getSmfTempo(data, event.extraDataOffset)
                            tempoChanges.add(TempoChange(absoluteTick, currentTempo))
                        }
                    }

                    // Note On
                    event.eventType.toUnsigned() == 0x90 && event.lsb.toUnsigned() > 0 -> {
                        val noteNumber = event.msb.toUnsigned()
                        val velocity = event.lsb.toUnsigned()
                        val channel = event.channel.toUnsigned()
                        activeNotes[noteNumber] = Triple(absoluteTick, velocity, channel)
                    }

                    // Note Off (or Note On with velocity 0)
                    event.eventType.toUnsigned() == 0x80 ||
                            (event.eventType.toUnsigned() == 0x90 && event.lsb.toUnsigned() == 0) -> {
                        val noteNumber = event.msb.toUnsigned()
                        val channel = event.channel.toUnsigned()

                        activeNotes.remove(noteNumber)?.let { (startTick, velocity, _) ->
                            val startTimeMs =
                                ticksToMilliseconds(startTick, currentTempo, ticksPerQuarter)
                            val endTimeMs =
                                ticksToMilliseconds(absoluteTick, currentTempo, ticksPerQuarter)

                            noteEvents.add(
                                MidiNoteEvent(
                                    noteNumber = noteNumber,
                                    velocity = velocity,
                                    startTick = startTick,
                                    endTick = absoluteTick,
                                    startTimeMs = startTimeMs,
                                    endTimeMs = endTimeMs,
                                    duration = endTimeMs - startTimeMs,
                                    channel = channel
                                )
                            )
                        }
                    }
                }
            }
        }

        // Calculate total duration
        val totalDurationMs = if (noteEvents.isNotEmpty()) {
            noteEvents.maxOf { it.endTimeMs }
        } else 0L

        MidiAnalysisResult(
            notes = noteEvents.sortedBy { it.startTimeMs },
            totalDurationMs = totalDurationMs,
            ticksPerQuarter = ticksPerQuarter,
            tempoChanges = tempoChanges
        )
    }

    private fun ticksToMilliseconds(
        ticks: Long,
        microsecondsPerQuarter: Int,
        ticksPerQuarter: Int
    ): Long {
        return (ticks * microsecondsPerQuarter / ticksPerQuarter / 1000)
    }

    // Extension function to convert Byte to unsigned Int
    private fun Byte.toUnsigned(): Int = this.toInt() and 0xFF
}
