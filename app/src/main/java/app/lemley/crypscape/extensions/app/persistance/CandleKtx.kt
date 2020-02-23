package app.lemley.crypscape.extensions.app.persistance

import app.lemley.crypscape.persistance.entities.Candle
import app.lemley.crypscape.persistance.entities.Granularity
import com.github.mikephil.charting.data.CandleEntry
import com.github.mikephil.charting.data.Entry

fun Candle.toChartEntry(granularity: Granularity):Entry {
    return CandleEntry(
        granularity.toXCoordinate(this.time),
        this.high.toFloat(),
        this.low.toFloat(),
        this.open.toFloat(),
        this.close.toFloat()
    )
}