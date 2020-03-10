package app.lemley.crypscape.charting

import app.lemley.crypscape.extensions.toInstant
import app.lemley.crypscape.persistance.entities.Candle
import app.lemley.crypscape.persistance.entities.Granularity
import app.lemley.crypscape.ui.market.candleSetLabel
import com.github.mikephil.charting.data.*
import com.google.common.truth.Truth.assertThat
import io.mockk.mockk
import org.junit.Test

class ChartRendererTest {

    @Test
    fun plot__defines_type_adding_to_data_set_with_given_label__replace_when_present() {

        val types =
            listOf(DataSetType.CandleDataSet, DataSetType.LineDataSet, DataSetType.ScatterDataSet)

        types.forEach { type ->
            val renderer = ChartRenderer()
            val set = renderer.plot(type, "label")
            val set2 = renderer.plot(type, "label")

            val data = when (type) {
                is DataSetType.CandleDataSet -> renderer.candleData
                is DataSetType.LineDataSet -> renderer.lineData
                is DataSetType.ScatterDataSet -> renderer.scatterData
                else -> throw IllegalArgumentException("implement more types")
            }

            assertThat(data.dataSetCount).isEqualTo(1)
            assertThat(data.dataSets[0]).isNotEqualTo(set)
            assertThat(data.dataSets[0]).isEqualTo(set2)

        }
    }

    @Test
    fun plot_entry__defines_limit_line() {
        val chartRenderer = ChartRenderer()
        chartRenderer.plot(DataSetType.CandleDataSet, "candles")

        val candle = Candle(
            platform_id = 1,
            product_id = 35,
            time = 1583856000000.toInstant(),
            granularity = Granularity.Hour,
            high = 7.0,
            low = 5.2,
            open = 5.9,
            close = 6.7,
            volume = 10.0
        )

        chartRenderer.limitFor(candle, mockk(relaxed = true))

        assertThat(chartRenderer.limitLine?.limit).isEqualTo(6.7F)

    }

    @Test
    fun plot_entry__updates_existing_entry__orders_by_x_coordinate() {
        val chartRenderer = ChartRenderer()

        data class Case(
            val type: DataSetType,
            val label: String,
            val entries: List<Entry>,
            val expectedEntries: List<Entry>
        )

        val cases = listOf<Case>(
            Case(
                DataSetType.CandleDataSet, candleSetLabel,
                listOf(
                    CandleEntry(1F, 100F, 90F, 91F, 98F),
                    CandleEntry(1F, 90F, 80F, 81F, 88F)
                ),
                listOf(
                    CandleEntry(1F, 90F, 80F, 81F, 88F)
                )
            )
            ,
            Case(
                DataSetType.LineDataSet, "line data",
                listOf(
                    Entry(1F, 100F),
                    Entry(1F, 90F)
                ),
                listOf(
                    Entry(1F, 90F)
                )
            ),
            Case(
                DataSetType.ScatterDataSet, "scatter data",
                listOf(
                    Entry(1F, 100F),
                    Entry(1F, 90F)
                ),
                listOf(
                    Entry(1F, 90F)
                )
            )
        )

        cases.forEach { testCase ->
            chartRenderer.plot(testCase.type, testCase.label)

            testCase.entries.forEach { entry ->
                chartRenderer.plotEntry(testCase.label, entry)
            }

            when (testCase.type) {
                is DataSetType.CandleDataSet -> {
                    val values = (chartRenderer.candleData.dataSets[0] as CandleDataSet).values
                    assertThat(values.size).isEqualTo(testCase.expectedEntries.size)
                    values.forEachIndexed { i, it ->
                        assertThat(values[i].x).isEqualTo(testCase.expectedEntries[i].x)
                        assertThat(values[i].y).isEqualTo(testCase.expectedEntries[i].y)
                    }
                }
                is DataSetType.LineDataSet -> {
                    val values = (chartRenderer.lineData.dataSets[0] as LineDataSet).values
                    assertThat(values.size).isEqualTo(testCase.expectedEntries.size)
                    values.forEachIndexed { i, it ->
                        assertThat(values[i].x).isEqualTo(testCase.expectedEntries[i].x)
                        assertThat(values[i].y).isEqualTo(testCase.expectedEntries[i].y)
                    }
                }
                is DataSetType.ScatterDataSet -> {
                    val values = (chartRenderer.scatterData.dataSets[0] as ScatterDataSet).values
                    assertThat(values.size).isEqualTo(testCase.expectedEntries.size)
                    values.forEachIndexed { i, it ->
                        assertThat(values[i].x).isEqualTo(testCase.expectedEntries[i].x)
                        assertThat(values[i].y).isEqualTo(testCase.expectedEntries[i].y)
                    }
                }
                else -> throw java.lang.IllegalArgumentException("implement more types")
            }
        }

    }

    @Test
    fun set_for_label__returns_given_data_set_with_label_value() {
        val chartRenderer = ChartRenderer()
        val candleLabel = "candle set"
        val lineLabel = "line set"
        val scatterLabel = "scatter set"

        chartRenderer.plot(DataSetType.CandleDataSet, candleLabel)
        chartRenderer.plot(DataSetType.LineDataSet, lineLabel)
        chartRenderer.plot(DataSetType.ScatterDataSet, scatterLabel)

        assertThat(chartRenderer.setForLabel(candleLabel) as CandleDataSet).isEqualTo(chartRenderer.candleData.dataSets[0])
        assertThat(chartRenderer.setForLabel(lineLabel) as LineDataSet).isEqualTo(chartRenderer.lineData.dataSets[0])
        assertThat(chartRenderer.setForLabel(scatterLabel) as ScatterDataSet).isEqualTo(
            chartRenderer.scatterData.dataSets[0]
        )
    }

    @Test
    fun clear_all_values__removes_all_data_sets() {
        val chartRenderer = ChartRenderer()
        chartRenderer.plot(DataSetType.CandleDataSet, "candle set 1")
        chartRenderer.plot(DataSetType.CandleDataSet, "candle set 2")
        chartRenderer.plot(DataSetType.LineDataSet, "line set 1")
        chartRenderer.plot(DataSetType.LineDataSet, "line set 2")
        chartRenderer.plot(DataSetType.LineDataSet, "line set 3")
        chartRenderer.plot(DataSetType.ScatterDataSet, "scatter set 1")
        chartRenderer.plot(DataSetType.ScatterDataSet, "scatter set 2")
        chartRenderer.plot(DataSetType.ScatterDataSet, "scatter set 3")
        chartRenderer.plot(DataSetType.ScatterDataSet, "scatter set 4")

        chartRenderer.clearAllData()

        assertThat(chartRenderer.candleData.dataSetCount).isEqualTo(0)
        assertThat(chartRenderer.lineData.dataSetCount).isEqualTo(0)
        assertThat(chartRenderer.scatterData.dataSetCount).isEqualTo(0)

    }

    @Test
    fun build_data__packages_data_sets() {
        val chartRenderer = ChartRenderer()

    }
}