package app.lemley.crypscape.charting

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
    fun plot_entry__adds_new_entry__orders_by_x_coordinate() {

    }

    @Test
    fun plot_entry__updates_existing_entry__orders_by_x_coordinate() {

    }

    @Test
    fun set_for_label__returns_given_data_set_with_label_value() {

    }

    @Test
    fun clear_all_values__removes_all_data_sets() {
    }

    @Test
    fun build_data__packages_data_sets() {
    }
}