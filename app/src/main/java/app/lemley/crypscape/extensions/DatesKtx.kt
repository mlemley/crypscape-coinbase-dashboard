package app.lemley.crypscape.extensions

import org.threeten.bp.Instant
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneOffset
import org.threeten.bp.ZoneOffset.UTC
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter


const val Iso8601Pattern: String = "yyyy-MM-dd'T'HH:mm:ss'Z'"
const val Iso8601PatternToMinute: String = "yyyy-MM-dd'T'HH:mm'Z'"

fun ZonedDateTime.toIso8601TimeStamp(pattern: String = Iso8601Pattern): String {
    val formatter = DateTimeFormatter.ofPattern(pattern)
    return formatter.format(this)
}

fun Instant.toEpochMinute(): Long = this.atZone(UTC).toEpochSecond() / 60

fun Instant.utc(): ZonedDateTime = this.atZone(UTC)

fun Instant.local(): ZonedDateTime = this.atZone(ZoneOffset.systemDefault())

fun String.toLocalTime() = toInstant()?.local()

fun Long.toLocalTime(): ZonedDateTime = this.toInstant().local()

fun ZonedDateTime.toMillis(): Long = this.toInstant().toEpochMilli()

fun String.toInstant(): Instant? {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH':'mm[':'ss[.SSSSSS]]'Z'")
    return LocalDateTime.parse(this, formatter).toInstant(UTC)
}


fun Long.toIso8601TimeStamp(): String = toInstant().utc().toIso8601TimeStamp()
fun Long.toIso8601TimeStampToMinute(): String =
    toInstant().utc().toIso8601TimeStamp(Iso8601PatternToMinute)

fun Long.toInstant(): Instant = Instant.ofEpochMilli(this)

fun Double.toInstant(): Instant = this.toLong().toInstantFromSeconds()

fun Float.toInstant(): Instant = this.toDouble().toInstant()

fun Long.toInstantFromSeconds(): Instant = Instant.ofEpochSecond(this)



