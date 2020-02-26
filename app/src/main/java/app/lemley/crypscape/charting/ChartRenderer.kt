package app.lemley.crypscape.charting

import androidx.annotation.VisibleForTesting
import app.lemley.crypscape.persistance.entities.Granularity
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.interfaces.datasets.IDataSet

class ChartRenderer {

    var granularity: Granularity = Granularity.Hour
    private val combinedData = CombinedData()

    private val setMap: MutableMap<String, DataSetType> = mutableMapOf()
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    val candleData = CandleData()
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    val lineData = LineData()
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    val scatterData = ScatterData()


    //ChartRendering

    @Suppress("UNCHECKED_CAST")
    fun plot(dataSetType: DataSetType, label: String): IDataSet<Entry>? {
        return when (dataSetType) {
            DataSetType.CandleDataSet -> {
                candleData.addDataSet(CandleDataSet(mutableListOf(), label))
                setMap.putIfAbsent(label, dataSetType)
                setForLabel(label)
            }
            DataSetType.LineDataSet -> {
                lineData.addDataSet(LineDataSet(mutableListOf(), label))
                setMap.putIfAbsent(label, dataSetType)
                setForLabel(label)
            }
            DataSetType.ScatterDataSet -> {
                scatterData.addDataSet(ScatterDataSet(mutableListOf(), label))
                setMap.putIfAbsent(label, dataSetType)
                setForLabel(label)
            }
            else -> null
        }
    }


    fun plotEntry(label: String, entry: Entry) {
        setForLabel(label)?.let { set ->
            set.getIndexInEntries(entry.x.toInt()).let { position ->
                if (position > 0) {
                    set.removeEntry(position)
                }
            }
            set.addEntry(entry)

            when (set) {
                is CandleDataSet -> set.values.sortBy { it.x }
                is LineDataSet -> set.values.sortBy { it.x }
                is BarDataSet -> set.values.sortBy { it.x }
                else -> {
                }
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun setForLabel(label: String): IDataSet<Entry>? {
        return when (setMap[label]) {
            DataSetType.CandleDataSet ->
                (candleData.getDataSetByLabel(label, false) as IDataSet<Entry>?)
            DataSetType.LineDataSet ->
                lineData.getDataSetByLabel(label, false)
            DataSetType.ScatterDataSet ->
                scatterData.getDataSetByLabel(label, false)
            else -> null
        }
    }

    fun clearAllData() {

    }

    fun buildData(): CombinedData {
        combinedData.setData(candleData)
        if (lineData.dataSetCount > 0)
            combinedData.setData(lineData)
        if (scatterData.dataSetCount > 0)
            combinedData.setData(scatterData)
        return combinedData
    }

}
