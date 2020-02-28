package app.lemley.crypscape.extensions.app.persistance

import app.lemley.crypscape.extensions.exhaustive
import app.lemley.crypscape.persistance.entities.Granularity
import org.threeten.bp.Instant
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.LocalTime
import org.threeten.bp.ZoneOffset.UTC

// Small granularities begin to over lap when creating coordinates using
// candle.time.toEpochSecond / granularity.seconds
// need to base the date off of a more current date or
// find a different way to represent the coordinate that can
// be mapped back to a valid xLabel
private val TimeScale: Instant = LocalDateTime.of(
    LocalDate.of(2017, 1, 1),
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


fun Granularity.toXCoordinate(instant: Instant): Float = scaleDown(instant, this).toFloat()

fun Granularity.fromXCoordinate(value: Float): Instant = scaleUp(value.toLong(), this)

private fun scaleUp(value: Long, granularity: Granularity): Instant {
    return Instant.ofEpochMilli(((value * granularity.seconds) * 1_000) + TimeScale.toEpochMilli())
}

private fun scaleDown(instant: Instant, granularity: Granularity): Long {
    return (instant.toEpochMilli() - TimeScale.toEpochMilli()) / 1_000 / granularity.seconds
}
