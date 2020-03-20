package app.lemley.crypscape.charting.operations

import app.lemley.crypscape.charting.ChartRenderer
import com.github.mikephil.charting.charts.LineChart

interface ILineChartOperations {
    fun operateWith(chart: LineChart, chartRenderer: ChartRenderer)
}
