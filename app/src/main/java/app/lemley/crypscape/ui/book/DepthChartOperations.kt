package app.lemley.crypscape.ui.book

import app.lemley.crypscape.charting.ChartRenderer
import app.lemley.crypscape.charting.DataSetType
import app.lemley.crypscape.charting.axis.DepthXAxisFormatter
import app.lemley.crypscape.charting.operations.ILineChartOperations
import app.lemley.crypscape.client.coinbase.model.DepthEntry
import app.lemley.crypscape.client.coinbase.model.OrderBook
import app.lemley.crypscape.extensions.configureForAsksDepth
import app.lemley.crypscape.extensions.configureForBidsDepth
import app.lemley.crypscape.extensions.configureForDepth
import app.lemley.crypscape.extensions.limitForDepth
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineDataSet


const val asksLabel = "asks"
const val bidsLabel = "bids"

sealed class DepthChartOperations : ILineChartOperations {
    object Clear : DepthChartOperations() {
        override fun operateWith(chart: LineChart, chartRenderer: ChartRenderer) {
            chart.data = null
            chartRenderer.clearAllData()
            chart.notifyDataSetChanged()
            chart.invalidate()
        }

    }

    object Configure : DepthChartOperations() {
        override fun operateWith(chart: LineChart, chartRenderer: ChartRenderer) {
            Clear.operateWith(chart, chartRenderer)
            chartRenderer.plot(DataSetType.LineDataSet, asksLabel)?.let { dataSet ->
                (dataSet as LineDataSet).configureForAsksDepth(chart)
            }
            chartRenderer.plot(DataSetType.LineDataSet, bidsLabel)?.let { dataSet ->
                (dataSet as LineDataSet).configureForBidsDepth(chart)
            }
            chart.configureForDepth()
        }
    }

    data class RenderDepth(val depth: OrderBook.Depth) : DepthChartOperations() {
        override fun operateWith(chart: LineChart, chartRenderer: ChartRenderer) {
            depth.bids.values.forEachIndexed { index, entry ->
                chartRenderer.plotEntry(
                    bidsLabel,
                    entry.toChartEntry(index)
                )
            }
            val bidsSize = depth.bids.size - 1
            depth.asks.values.forEachIndexed { index, entry ->
                chartRenderer.plotEntry(
                    label = asksLabel,
                    entry = entry.toChartEntry(index + bidsSize)
                )
            }
            val lineData = chartRenderer.lineData
            chart.data = lineData
            updateDataSetVisibility(chart)
            chart.isAutoScaleMinMaxEnabled = true
            chart.xAxis.apply {
                valueFormatter = DepthXAxisFormatter(depth)
                removeAllLimitLines()
                addLimitLine(LimitLine(bidsSize.toFloat()).also {
                    it.labelPosition = LimitLine.LimitLabelPosition.LEFT_TOP
                    it.limitForDepth(chart)
                })
            }
            chart.notifyDataSetChanged()
            chart.invalidate()
        }

        private fun updateDataSetVisibility(chart: LineChart) {
            chart.data?.dataSets?.forEach { dataSet ->
                dataSet.isVisible = dataSet.entryCount != 0
            }
        }
    }

    fun DepthEntry.toChartEntry(index: Int): Entry = Entry(index.toFloat(), depth, this)
}