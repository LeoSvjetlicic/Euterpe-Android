package ls.diplomski.euterpe.utils

import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAccessor

object LocalDateTimeFormatter {
    private val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

    // Accepts anything that implements TemporalAccessor (like LocalDate, LocalDateTime)
    fun toFormattedString(date: TemporalAccessor): String {
        return formatter.format(date)
    }
}