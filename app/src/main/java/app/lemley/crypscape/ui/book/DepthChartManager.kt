package app.lemley.crypscape.ui.book

import app.lemley.crypscape.charting.ChartRenderer
import app.lemley.crypscape.charting.operations.ILineChartOperations
import com.github.mikephil.charting.charts.LineChart

class DepthChartManager(
    val chartRenderer: ChartRenderer
) {

    fun performChartingOperation(chart: LineChart?, operation: ILineChartOperations) {
        chart?.let { operation.operateWith(it, chartRenderer) }
    }
}