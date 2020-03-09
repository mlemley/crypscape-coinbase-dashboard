package app.lemley.crypscape.ui.market

import app.lemley.crypscape.charting.ChartRenderer
import app.lemley.crypscape.charting.DataSetType
import app.lemley.crypscape.extensions.app.persistance.toChartEntry
import app.lemley.crypscape.extensions.app.persistance.visibleXRange
import app.lemley.crypscape.extensions.toInstant
import app.lemley.crypscape.persistance.entities.Candle
import app.lemley.crypscape.persistance.entities.Granularity
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.interfaces.datasets.IDataSet
import io.mockk.*
import org.junit.Ignore
import org.junit.Test

class ChartOperationsTest {

    @Test
    fun clears_chart_and_all_of_its_data() {
        val chart: CombinedChart = mockk(relaxUnitFun = true)
        val chartRenderer: ChartRenderer = mockk(relaxUnitFun = true)

        ChartOperations.Clear.operateWith(chart, chartRenderer)

        verifyOrder {
            chart.data = null
            chartRenderer.clearAllData()
            chart.notifyDataSetChanged()
            chart.invalidate()
        }

        confirmVerified(chart, chartRenderer)
    }

    @Test
    fun configures_chart_with_specified_granularity__configures__when_granularity_null() {
        val chart: CombinedChart = mockk(relaxed = true)
        val chartRenderer: ChartRenderer = mockk(relaxUnitFun = true) {
            every {
                plot(
                    DataSetType.CandleDataSet,
                    candleSetLabel
                )
            } returns null
            every { granularity } returns null
        }
        val granularity = Granularity.Hour

        ChartOperations.ConfigureFor(granularity).operateWith(chart, chartRenderer)

        excludeRecords {
            chartRenderer.granularity
        }

        verifyOrder {
            chartRenderer.clearAllData()
            chartRenderer.granularity = granularity
            chartRenderer.plot(DataSetType.CandleDataSet, candleSetLabel)
        }

        confirmVerified(chartRenderer)
    }

    @Test
    fun configures_chart_with_specified_granularity__configures__when_granularity_not_the_same() {
        val chart: CombinedChart = mockk(relaxed = true)
        val chartRenderer: ChartRenderer = mockk(relaxUnitFun = true) {
            every {
                plot(
                    DataSetType.CandleDataSet,
                    candleSetLabel
                )
            } returns null
            every { granularity } returns Granularity.Day
        }
        val granularity = Granularity.Hour

        excludeRecords {
            chartRenderer.granularity
        }

        ChartOperations.ConfigureFor(granularity).operateWith(chart, chartRenderer)

        verifyOrder {
            chartRenderer.clearAllData()
            chartRenderer.granularity = granularity
            chartRenderer.plot(DataSetType.CandleDataSet, candleSetLabel)
        }

        confirmVerified(chartRenderer)
    }

    @Test
    fun configures_chart_with_specified_granularity__does_nothing__when_granularity_is_the_same() {
        val chart: CombinedChart = mockk(relaxed = true)
        val chartRenderer: ChartRenderer = mockk(relaxUnitFun = true) {
            every {
                plot(
                    DataSetType.CandleDataSet,
                    candleSetLabel
                )
            } returns null
            every { granularity } returns Granularity.Hour
        }
        val granularity = Granularity.Hour

        excludeRecords {
            chartRenderer.granularity
        }

        ChartOperations.ConfigureFor(granularity).operateWith(chart, chartRenderer)

        confirmVerified(chartRenderer)
    }

    @Ignore
    @Test
    fun renders_provided_candles() {
        val configuredGranularity = Granularity.Hour
        val moveToXValue = 1.0F
        val combinedData = mockk<CombinedData>(relaxed = true) {
            every { candleData } returns mockk {
                every { dataSets } returns mutableListOf(mockk {
                    every { xMax } returns moveToXValue
                })
            }
        }
        val xaxis: XAxis = mockk(relaxUnitFun = true)
        val chart: CombinedChart = mockk(relaxUnitFun = true) {
            every { xAxis } returns xaxis
            every { data } returns null
        }
        val chartRenderer: ChartRenderer = mockk(relaxUnitFun = true) {
            every { granularity } returns configuredGranularity
            every { buildData() } returns combinedData
        }
        val candle1 = Candle(
            high = 110.0,
            low = 100.0,
            open = 105.0,
            close = 109.0,
            time = 1415398768000.toInstant(),
            volume = 10.0,
            granularity = configuredGranularity,
            platform_id = 1,
            product_id = 2
        )
        val candle1Entry = candle1.toChartEntry()

        val candle2 = Candle(
            high = 210.0,
            low = 200.0,
            open = 205.0,
            close = 209.0,
            time = 1415308787000.toInstant(),
            volume = 20.0,
            granularity = configuredGranularity,
            platform_id = 1,
            product_id = 2
        )
        val candle2Entry = candle2.toChartEntry()

        val candles: List<Candle> = listOf(candle1, candle2)

        excludeRecords {
            chartRenderer.buildData()
            chartRenderer.granularity
            chart.data
            chart.xAxis
        }

        ChartOperations.RenderCandles(candles).operateWith(chart, chartRenderer)

        verifyOrder {
            chartRenderer.plotEntry(candleSetLabel, candle1Entry)
            chartRenderer.plotEntry(candleSetLabel, candle2Entry)
            chart.data = combinedData
            chart.setVisibleXRangeMaximum(configuredGranularity.visibleXRange)
            chart.isAutoScaleMinMaxEnabled = true
            xaxis.setAvoidFirstLastClipping(true)
            chart.moveViewToX(moveToXValue)
            chart.notifyDataSetChanged()
            chart.invalidate()
        }
    }
}