package app.lemley.crypscape.extensions.app.persistance

import androidx.core.content.ContextCompat
import app.lemley.crypscape.extensions.exhaustive
import app.lemley.crypscape.extensions.toEpochMinute
import app.lemley.crypscape.extensions.utc
import app.lemley.crypscape.persistance.entities.Granularity
import org.threeten.bp.Instant
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.LocalTime
import org.threeten.bp.ZoneOffset.UTC

private val MINUTE_SCALE: Instant = LocalDateTime.of(
    LocalDate.of(2009, 1, 1),
    LocalTime.of(0, 0)
).toInstant(UTC)

val Granularity.xAxisLabelCount: Int
    get() = when (this) {
        Granularity.Minute -> 14
        Granularity.FiveMinutes -> 14
        Granularity.FifteenMinutes -> 14
        Granularity.Hour -> 14
        Granularity.SixHours -> 14
        Granularity.Day -> 14
    }.exhaustive

val Granularity.visibleXRange: Float
    get() = when (this) {
/*
        Granularity.Minute -> 140.toFloat()
        Granularity.FiveMinutes -> 150.toFloat() // 1/2 day
        Granularity.FifteenMinutes -> 100.toFloat()
        else -> {
            (5 * 24 + Instant.now().atZone(ZoneId.systemDefault()).hour).toFloat()
        }
*/
        else -> 65F
    }

fun scaleMinuteDown(instant: Instant): Long {
    return (instant.toEpochMinute() - MINUTE_SCALE.toEpochMinute())
}

fun Granularity.toXCoordinate(instant: Instant): Float {
    return when (this) {
        Granularity.Minute -> scaleMinuteDown(instant).toFloat()
        Granularity.FiveMinutes -> (instant.utc().toEpochSecond() / Granularity.FiveMinutes.seconds).toFloat()
        Granularity.FifteenMinutes -> (instant.utc().toEpochSecond() / Granularity.FifteenMinutes.seconds).toFloat()
        Granularity.Hour -> (instant.utc().toEpochSecond() / Granularity.Hour.seconds).toFloat()
        Granularity.Day -> (instant.utc().toEpochSecond() / Granularity.Day.seconds).toFloat()
        Granularity.SixHours -> (instant.utc().toEpochSecond() / Granularity.SixHours.seconds).toFloat()
    }.exhaustive
}

fun Granularity.fromXCoordinate(value: Float): Instant {
    return when (this) {
        Granularity.Minute -> minuteFromScaledValue(value.toLong())
        Granularity.FiveMinutes -> Instant.ofEpochSecond(value.toLong() * Granularity.FiveMinutes.seconds)
        Granularity.FifteenMinutes -> Instant.ofEpochSecond(value.toLong() * Granularity.FifteenMinutes.seconds)
        Granularity.Hour -> Instant.ofEpochSecond(value.toLong() * Granularity.Hour.seconds)
        Granularity.Day -> Instant.ofEpochSecond(value.toLong() * Granularity.Day.seconds)
        Granularity.SixHours -> Instant.ofEpochSecond(value.toLong() * Granularity.SixHours.seconds)
    }.exhaustive
}

private fun minuteFromScaledValue(value: Long): Instant {
    return Instant.ofEpochSecond((value + MINUTE_SCALE.toEpochMinute()) * 60)
}
