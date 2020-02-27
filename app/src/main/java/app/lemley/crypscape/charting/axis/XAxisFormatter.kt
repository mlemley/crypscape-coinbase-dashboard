package app.lemley.crypscape.charting.axis

import app.lemley.crypscape.extensions.app.persistance.fromXCoordinate
import app.lemley.crypscape.extensions.local
import app.lemley.crypscape.extensions.utc
import app.lemley.crypscape.persistance.entities.Granularity
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import org.threeten.bp.format.TextStyle
import java.util.*


class XAxisFormatter constructor(val granularity: Granularity) : IAxisValueFormatter {
    override fun getFormattedValue(value: Float, axis: AxisBase?): String {
        val dateTime =
            if (granularity == Granularity.Day) granularity.fromXCoordinate(value).utc()
            else granularity.fromXCoordinate(value).local()
        val dayOfMonth = dateTime.dayOfMonth
        val hourInDay = dateTime.hour
        val minuteOfHour = dateTime.minute
        return if (dayOfMonth == 1 && hourInDay == 0)
            "${dateTime.month.getDisplayName(TextStyle.SHORT, Locale.getDefault())} 1"
        else if (granularity == Granularity.Day)
            "${dateTime.monthValue}/$dayOfMonth"
        else if (hourInDay == 0)
            "${dateTime.monthValue}/${dateTime.dayOfMonth}"
        else if (shouldShowMinutes())
            if (minuteOfHour < 10)
                "$hourInDay:0$minuteOfHour"
            else
                "$hourInDay:$minuteOfHour"
        else
            "$hourInDay:00"
    }

    private fun shouldShowMinutes(): Boolean = when (granularity) {
        Granularity.FifteenMinutes,
        Granularity.FiveMinutes,
        Granularity.Minute -> true
        else -> false
    }

}