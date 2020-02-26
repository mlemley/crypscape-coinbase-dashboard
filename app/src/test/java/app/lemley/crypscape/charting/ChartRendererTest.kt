package app.lemley.crypscape.charting

import app.lemley.crypscape.ui.market.candleSetLabel
import com.github.mikephil.charting.data.*
import com.google.common.truth.Truth.assertThat
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
    fun plot_entry__adds_new_entry__orders_by_x_coordinate__candle_data() {
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
                    CandleEntry(2F, 100F, 90F, 91F, 98F),
                    CandleEntry(1F, 90F, 80F, 81F, 88F)
                ),
                listOf(
                    CandleEntry(1F, 90F, 80F, 81F, 88F),
                    CandleEntry(2F, 100F, 90F, 91F, 98F)
                )
            ),
            Case(
                DataSetType.LineDataSet, "line data",
                listOf(
                    Entry(2F, 100F),
                    Entry(1F, 90F)
                ),
                listOf(
                    Entry(1F, 90F),
                    Entry(2F, 100F)
                )
            ),
            Case(
                DataSetType.ScatterDataSet, "scatter data",
                listOf(
                    Entry(2F, 100F),
                    Entry(1F, 90F)
                ),
                listOf(
                    Entry(1F, 90F),
                    Entry(2F, 100F)
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
                    values.forEachIndexed { i, it ->
                        assertThat(values[i].x).isEqualTo(testCase.expectedEntries[i].x)
                        assertThat(values[i].y).isEqualTo(testCase.expectedEntries[i].y)
                    }
                }
                is DataSetType.LineDataSet -> {
                    val values = (chartRenderer.lineData.dataSets[0] as LineDataSet).values
                    values.forEachIndexed { i, it ->
                        assertThat(values[i].x).isEqualTo(testCase.expectedEntries[i].x)
                        assertThat(values[i].y).isEqualTo(testCase.expectedEntries[i].y)
                    }
                }
                is DataSetType.ScatterDataSet -> {
                    val values = (chartRenderer.scatterData.dataSets[0] as ScatterDataSet).values
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
    fun plot_entry__updates_existing_entry__orders_by_x_coordinate() {

    }

    @Test
    fun set_for_label__returns_given_data_set_with_label_value() {

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
    }
}