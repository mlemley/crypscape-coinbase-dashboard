package app.lemley.crypscape.ui.market

import app.lemley.crypscape.charting.ChartRenderer
import app.lemley.crypscape.charting.DataSetType
import app.lemley.crypscape.extensions.app.persistance.toChartEntry
import app.lemley.crypscape.extensions.app.persistance.visibleXRange
import app.lemley.crypscape.extensions.configureForCrypScape
import app.lemley.crypscape.persistance.entities.Candle
import app.lemley.crypscape.persistance.entities.Granularity
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.data.CandleDataSet

interface ICombinedChartOperation {
    fun operateWith(chart: CombinedChart, chartRenderer: ChartRenderer)
}

const val candleSetLabel = "candles"

sealed class ChartOperations : ICombinedChartOperation {
    object Clear : ChartOperations() {
        override fun operateWith(chart: CombinedChart, chartRenderer: ChartRenderer) {
            chart.data = null
            chartRenderer.clearAllData()
            chart.notifyDataSetChanged()
            chart.invalidate()
        }

    }

    data class ConfigureFor(val granularity: Granularity) : ChartOperations() {
        override fun operateWith(chart: CombinedChart, chartRenderer: ChartRenderer) {
            if (chartRenderer.granularity != granularity) {
                Clear.operateWith(chart, chartRenderer)
                chartRenderer.granularity = granularity
                chartRenderer.plot(DataSetType.CandleDataSet, candleSetLabel)?.let { dataSet ->
                    (dataSet as CandleDataSet).configureForCrypScape(chart)
                }
                chart.configureForCrypScape(granularity)
            }
        }
    }

    data class RenderCandles(val candles: List<Candle>) : ICombinedChartOperation {
        override fun operateWith(chart: CombinedChart, chartRenderer: ChartRenderer) {
            chart.axisLeft.removeAllLimitLines()
            candles.forEach { candle ->
                chartRenderer.plotEntry(
                    candleSetLabel,
                    candle.toChartEntry()
                )
            }
            chartRenderer.limitFor(candles.last(), chart.context)
            val combinedData = chartRenderer.buildData()
            chart.data = combinedData
            updateDataSetVisibility(chart)
            chart.setVisibleXRangeMaximum(
                chartRenderer.granularity?.visibleXRange(chart.context) ?: Granularity.Hour.visibleXRange(chart.context)
            )
            chart.isAutoScaleMinMaxEnabled = true
            chart.axisLeft.apply {
                addLimitLine(chartRenderer.limitLine)
            }
            chart.moveViewToX(combinedData.candleData.dataSets[0].xMax)
            chart.notifyDataSetChanged()
            chart.invalidate()
        }

        private fun updateDataSetVisibility(chart: CombinedChart) {
            chart.data?.candleData?.dataSets?.forEach { dataSet ->
                dataSet.isVisible = dataSet.entryCount != 0
            }

            chart.data?.lineData?.dataSets?.forEach { dataSet ->
                dataSet.isVisible = dataSet.entryCount != 0
            }

            chart.data?.scatterData?.dataSets?.forEach { dataSet ->
                dataSet.isVisible = dataSet.entryCount != 0
            }
        }
    }
}
