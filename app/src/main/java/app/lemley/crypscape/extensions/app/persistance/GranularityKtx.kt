package app.lemley.crypscape.extensions.app.persistance

import android.content.Context
import android.content.res.Configuration
import app.lemley.crypscape.extensions.*
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

fun Granularity.xAxisLabelCount(context: Context): Int {
    return if (context.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
        14
    } else {
        7
    }
}

fun Granularity.visibleXRange(context: Context): Float {
    return if (context.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
        when (this) {
            Granularity.Minute -> 140.toFloat()
            Granularity.FiveMinutes -> 150.toFloat() // 1/2 day
            Granularity.FifteenMinutes -> 100.toFloat()
            else -> 65F

        }
    } else {
        35F
    }
}


fun Granularity.toXCoordinate(instant: Instant): Float = scaleDown(instant, this).toFloat()

fun Granularity.fromXCoordinate(value: Float): Instant = scaleUp(value.toLong(), this)

private fun scaleUp(value: Long, granularity: Granularity): Instant {
    return Instant.ofEpochMilli(((value * granularity.seconds) * 1_000) + TimeScale.toEpochMilli())
}

private fun scaleDown(instant: Instant, granularity: Granularity): Long {
    return (instant.toEpochMilli() - TimeScale.toEpochMilli()) / 1_000 / granularity.seconds
}

fun Granularity.periodForTickerTime(time: String): Instant {
    return (time.toInstant() ?: Instant.now()).let {
        when (this) {
            Granularity.Day -> it.toEpochMinute().fromEpochMinute().utc()
                .minusSeconds(it.utc().minute * 60L)
                .minusHours(it.utc().hour.toLong())
                .toInstant()

            Granularity.SixHours -> it.toEpochMinute().fromEpochMinute().utc()
                .minusSeconds(it.utc().minute * 60L)
                .minusHours(it.utc().hour % (this.seconds / 60 / 60))
                .toInstant()

            else -> it.toEpochMinute().fromEpochMinute()
                .minusSeconds((it.utc().minute % (this.seconds / 60L)) * 60L)
        }
    }
}

fun Granularity.previousPeriod(instant: Instant): Instant {
    return instant.minusSeconds(this.seconds)
}

