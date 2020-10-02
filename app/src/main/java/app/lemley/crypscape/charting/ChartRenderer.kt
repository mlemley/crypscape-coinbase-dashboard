package app.lemley.crypscape.charting

import android.content.Context
import androidx.annotation.VisibleForTesting
import app.lemley.crypscape.R
import app.lemley.crypscape.persistance.entities.Candle
import app.lemley.crypscape.persistance.entities.Granularity
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.data.*

import com.github.mikephil.charting.interfaces.datasets.IDataSet

class ChartRenderer {

    var limitLine: LimitLine? = null
    var granularity: Granularity? = null
    private val combinedData = CombinedData()

    private val setMap: MutableMap<String, DataSetType> = mutableMapOf()

    @VisibleForTesting
    val candleData = CandleData()

    @VisibleForTesting
    val lineData = LineData()

    @VisibleForTesting
    val scatterData = ScatterData()


    //ChartRendering

    @Suppress("UNCHECKED_CAST")
    fun plot(dataSetType: DataSetType, label: String): IDataSet<Entry>? {
        return when (dataSetType) {
            DataSetType.CandleDataSet -> {
                setForLabel(label)?.let {
                    candleData.removeDataSet(it as CandleDataSet)
                }
                candleData.addDataSet(CandleDataSet(mutableListOf(), label))
                setMap.putIfAbsent(label, dataSetType)
                setForLabel(label)
            }
            DataSetType.LineDataSet -> {
                setForLabel(label)?.let {
                    lineData.removeDataSet(it as LineDataSet)
                }
                lineData.addDataSet(LineDataSet(mutableListOf(), label))
                setMap.putIfAbsent(label, dataSetType)
                setForLabel(label)
            }
            DataSetType.ScatterDataSet -> {
                setForLabel(label)?.let {
                    scatterData.removeDataSet(it as ScatterDataSet)
                }
                scatterData.addDataSet(ScatterDataSet(mutableListOf(), label))
                setMap.putIfAbsent(label, dataSetType)
                setForLabel(label)
            }
            else -> null
        }
    }


    fun plotEntry(label: String, entry: Entry) {
        setForLabel(label)?.let { set ->
            removeEntryAtXCoordinateIfPresent(set, entry)
            set.addEntry(entry)

            when (set) {
                is CandleDataSet -> set.values.sortBy { it.x }
                is LineDataSet -> set.values.sortBy { it.x }
                is ScatterDataSet -> set.values.sortBy { it.x }
                else -> {
                }
            }
        }
    }

    private fun removeEntryAtXCoordinateIfPresent(set: IDataSet<Entry>, entry: Entry) {
        when (set) {
            is LineDataSet -> removeLineEntryAtXCoordinateIfPresent(set, entry)
            is ScatterDataSet -> removeScatterEntryAtXCoordinateIfPresent(set, entry)
            is CandleDataSet -> removeCandleEntryAtXCoordinateIfPresent(set, entry)
        }
    }


    private fun removeScatterEntryAtXCoordinateIfPresent(set: ScatterDataSet, entry: Entry) {
        var indexToRemove: Int? = null
        set.values.forEachIndexed { i, it ->
            if (it.x == entry.x) {
                indexToRemove = i
                return@forEachIndexed
            }
        }
        indexToRemove?.let { set.removeEntry(it) }
    }

    private fun removeLineEntryAtXCoordinateIfPresent(set: LineDataSet, entry: Entry) {
        var indexToRemove: Int? = null
        set.values.forEachIndexed { i, it ->
            if (it.x == entry.x) {
                indexToRemove = i
                return@forEachIndexed
            }
        }
        indexToRemove?.let { set.removeEntry(it) }
    }

    private fun removeCandleEntryAtXCoordinateIfPresent(set: CandleDataSet, entry: Entry) {
        var indexToRemove: Int? = null
        set.values.forEachIndexed { i, it ->
            if (it.x == entry.x) {
                indexToRemove = i
                return@forEachIndexed
            }
        }
        indexToRemove?.let { set.removeEntry(it) }
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
        while (candleData.dataSetCount > 0 || lineData.dataSetCount > 0 || scatterData.dataSetCount > 0) {
            candleData.removeDataSet(0)
            lineData.removeDataSet(0)
            scatterData.removeDataSet(0)
        }
    }

    fun buildData(): CombinedData {
        combinedData.setData(candleData)
        if (lineData.dataSetCount > 0)
            combinedData.setData(lineData)
        if (scatterData.dataSetCount > 0)
            combinedData.setData(scatterData)
        return combinedData
    }

    fun limitFor(candle: Candle, context: Context) {
        limitLine = LimitLine(candle.close.toFloat()).also {
            it.enableDashedLine(2F, 3F, 0F)
            it.lineWidth = 2F
            it.lineColor =
                if (candle.open < candle.close) context.getColor(R.color.candlestick_increasing) else context.getColor(
                    R.color.candlestick_decreasing
                )
            it.labelPosition = LimitLine.LimitLabelPosition.LEFT_TOP
        }
    }

}
