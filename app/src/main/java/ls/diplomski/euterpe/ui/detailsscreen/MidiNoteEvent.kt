package ls.diplomski.euterpe.ui.detailsscreen

data class MidiNoteEvent(
    val noteNumber: Int,
    val velocity: Int,
    val startTick: Long,
    val endTick: Long,
    val startTimeMs: Long,
    val endTimeMs: Long,
    val duration: Long,
    val channel: Int = 0
) {
    fun getNoteName(): String {
        val noteNames = arrayOf("C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B")
        val octave = (noteNumber / 12) - 1
        val note = noteNames[noteNumber % 12]
        return "$note$octave"
    }
}

data class MidiAnalysisResult(
    val notes: List<MidiNoteEvent>,
    val totalDurationMs: Long,
    val ticksPerQuarter: Int,
    val tempoChanges: List<TempoChange>
)

data class TempoChange(
    val tick: Long,
    val microsecondsPerQuarter: Int,
    val bpm: Double = 60_000_000.0 / microsecondsPerQuarter
)

data class MidiAnalysisUiState(
    val isLoading: Boolean = false,
    val analysisResult: MidiAnalysisResult? = null,
    val error: String? = null,
    val isPlaying: Boolean = false,
    val currentTimeMs: Long = 0L,
    val activeNotes: Set<Int> = emptySet()
)

object MidiMetaType {
    const val TEMPO = 0x51
    const val TIME_SIGNATURE = 0x58
    const val KEY_SIGNATURE = 0x59
}
