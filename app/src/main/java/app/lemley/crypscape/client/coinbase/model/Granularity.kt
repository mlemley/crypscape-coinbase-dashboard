package app.lemley.crypscape.client.coinbase.model

import java.time.temporal.ChronoUnit

enum class Granularity constructor(val seconds: Long, val chronoUnit: ChronoUnit) {
    Minute(60, ChronoUnit.MINUTES),
    FiveMinutes(300, ChronoUnit.MINUTES),
    FifteenMinutes(900, ChronoUnit.MINUTES),
    Hour(3600, ChronoUnit.HOURS),
    SixHours(21600, ChronoUnit.HOURS),
    Day(86400, ChronoUnit.DAYS);

    companion object {
        fun fromSeconds(seconds:Long): Granularity = when (seconds){
            Minute.seconds -> Minute
            FiveMinutes.seconds -> FiveMinutes
            FifteenMinutes.seconds -> FifteenMinutes
            SixHours.seconds -> SixHours
            Day.seconds -> Day
            else -> Hour
        }
    }
}