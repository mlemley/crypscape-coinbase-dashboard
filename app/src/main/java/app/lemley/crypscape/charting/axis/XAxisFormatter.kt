package app.lemley.crypscape.charting.axis

import app.lemley.crypscape.persistance.entities.Granularity
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.IAxisValueFormatter


class XAxisFormatter constructor(granularity: Granularity) :IAxisValueFormatter {
    override fun getFormattedValue(value: Float, axis: AxisBase?): String {
        return value.toLong().toString()
    }

}