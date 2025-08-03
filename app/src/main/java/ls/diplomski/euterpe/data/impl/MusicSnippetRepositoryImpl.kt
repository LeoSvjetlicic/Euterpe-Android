package ls.diplomski.euterpe.data.impl

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ls.diplomski.euterpe.data.MusicSnippet
import ls.diplomski.euterpe.domain.MusicSnippetsRepository
import java.io.File
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.util.Date
import java.util.Locale

class MusicSnippetRepositoryImpl(
    private val context: Context
) : MusicSnippetsRepository {

    override suspend fun fetchMusicSnippets(): List<MusicSnippet> = withContext(Dispatchers.IO) {
        val midiFiles = getMidiFilesFromStorage()
        midiFiles.map { file ->
            createMusicSnippetFromFile(file)
        }.sortedByDescending { it.dateCreated } // Most recent first
    }

    override suspend fun fetchMusicSnippetById(name: String): MusicSnippet? =
        withContext(Dispatchers.IO) {
            val midiFiles = getMidiFilesFromStorage()
            val targetFile = midiFiles.find { file ->
                file.name == name
            }

            targetFile?.let { createMusicSnippetFromFile(it) }
        }

    private fun getMidiFilesFromStorage(): List<File> {
        // Get the directory where MIDI files are stored
        val midiDir = File(context.getExternalFilesDir(null), "midi_files")

        // Return empty list if directory doesn't exist
        if (!midiDir.exists()) {
            return emptyList()
        }

        // Filter files to only include .mid and .midi files
        return midiDir.listFiles { file ->
            file.isFile && (file.extension.equals("mid", ignoreCase = true) ||
                    file.extension.equals("midi", ignoreCase = true))
        }?.toList() ?: emptyList()
    }

    private fun createMusicSnippetFromFile(file: File): MusicSnippet {
        // Extract ID from filename (assuming format: midi_ID_timestamp.mid)
        val id = extractIdFromFileName(file.name)

        // Get file creation date from filename or file modification date
        val createdDate = extractDateFromFileName(file.name) ?: Date(file.lastModified())

        return MusicSnippet(
            id = id,
            filePath = file.absolutePath,
            name = file.name,
            dateCreated = createdDate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate(),
        )
    }

    private fun extractIdFromFileName(fileName: String): String {
        // Assuming filename format: midi_ID_timestamp.mid
        // Extract the ID part (middle section)
        return try {
            val nameWithoutExtension = fileName.substringBeforeLast('.')
            val parts = nameWithoutExtension.split('_')
            if (parts.size >= 3) {
                parts[1] // Return the ID part
            } else {
                fileName.substringBeforeLast('.') // Fallback to full name without extension
            }
        } catch (e: Exception) {
            fileName.substringBeforeLast('.') // Fallback
        }
    }

    private fun extractDateFromFileName(fileName: String): Date? {
        return try {
            // Assuming filename format: midi_ID_yyyyMMdd_HHmmss.mid
            val nameWithoutExtension = fileName.substringBeforeLast('.')
            val parts = nameWithoutExtension.split('_')

            if (parts.size >= 4) {
                val datePart = parts[2] // yyyyMMdd
                val timePart = parts[3] // HHmmss
                val dateTimeString = "${datePart}_$timePart"

                val formatter = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)
                formatter.parse(dateTimeString)
            } else {
                null
            }
        } catch (e: Exception) {
            null // Return null if parsing fails
        }
    }
}
