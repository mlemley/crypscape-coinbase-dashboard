package app.lemley.crypscape.extensions.app.persistance

import app.lemley.crypscape.persistance.entities.Candle
import com.github.mikephil.charting.data.CandleEntry
import com.github.mikephil.charting.data.Entry

fun Candle.toChartEntry(): Entry {
    return CandleEntry(
        granularity.toXCoordinate(this.time),
        high.toFloat(),
        low.toFloat(),
        open.toFloat(),
        close.toFloat()
    )
}