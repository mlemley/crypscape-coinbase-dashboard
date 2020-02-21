package app.lemley.crypscape.extensions.app.persistance

import app.lemley.crypscape.extensions.exhaustive
import app.lemley.crypscape.extensions.toEpochMinute
import app.lemley.crypscape.extensions.toInstant
import app.lemley.crypscape.persistance.entities.Granularity
import org.threeten.bp.*
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
        Granularity.Minute -> 140.toFloat()
        Granularity.FiveMinutes -> 150.toFloat() // 1/2 day
        Granularity.FifteenMinutes -> 100.toFloat()
        else -> {
            (5 * 24 + Instant.now().atZone(ZoneId.systemDefault()).hour).toFloat()
        }
    }

fun scaleMinuteDown(instant: Instant): Long {
    return (instant.toEpochMinute() - MINUTE_SCALE.toEpochMinute())
}

fun Granularity.toXCoordinate(instant: Instant): Float {
    return if (this == Granularity.Minute)
        scaleMinuteDown(instant).toFloat()
    else
        (instant.atZone(UTC).toEpochSecond() / seconds).toFloat()

}

fun Granularity.fromXCoordinate(value: Float): Instant {
    return if (this == Granularity.Minute)
        minuteFromScaledValue(value.toLong())
    else
        (value.toLong() * seconds * 1000).toInstant()
}

private fun minuteFromScaledValue(value: Long): Instant {
    return Instant.ofEpochSecond((value + MINUTE_SCALE.toEpochMinute()) * 60)
}
