package app.lemley.crypscape.charting.axis

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.IAxisValueFormatter


class YAxisFormatter :IAxisValueFormatter {
    override fun getFormattedValue(value: Float, axis: AxisBase?): String {
        return value.toLong().toString()
    }

}