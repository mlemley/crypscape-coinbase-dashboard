package app.lemley.crypscape.ui.market

import app.lemley.crypscape.charting.ChartRenderer
import com.github.mikephil.charting.charts.CombinedChart

class MarketChartingManager constructor(val chartRenderer: ChartRenderer) {

    fun performChartingOperation(chart: CombinedChart?, operation: IChartOperation) {
        chart?.let { operation.operateWith(it, chartRenderer) }
    }

}
