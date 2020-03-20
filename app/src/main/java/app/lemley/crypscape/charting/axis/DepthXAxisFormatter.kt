package app.lemley.crypscape.charting.axis

import app.lemley.crypscape.client.coinbase.model.OrderBook
import app.lemley.crypscape.extensions.app.toDecimalFormat
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.IAxisValueFormatter


class DepthXAxisFormatter constructor(
    private val depth: OrderBook.Depth
) : IAxisValueFormatter {
    override fun getFormattedValue(value: Float, axis: AxisBase?): String {
        val position = value.toInt()
        val numBids = depth.bids.size
        return if (position > numBids)
            depth.asks.values.toList()[position - numBids].price.toDecimalFormat("#,##0.00#")
        else
            depth.bids.values.toList()[position].price.toDecimalFormat("#,##0.00#")
    }

}