package app.lemley.crypscape.charting.axis

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import java.text.DecimalFormat


class YAxisFormatter :IAxisValueFormatter {
    private val formatter = DecimalFormat.getNumberInstance()
    override fun getFormattedValue(value: Float, axis: AxisBase?): String {
        return formatter.format(value.toLong())
    }

}