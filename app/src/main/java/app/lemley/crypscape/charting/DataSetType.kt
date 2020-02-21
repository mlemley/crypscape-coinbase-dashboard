package app.lemley.crypscape.charting


sealed class DataSetType {
    object LineDataSet: DataSetType()
    object CandleDataSet: DataSetType()
    object ScatterDataSet: DataSetType()
    object BarDataSet: DataSetType()
}