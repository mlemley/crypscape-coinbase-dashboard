package app.lemley.crypscape.charting.axis

import app.lemley.crypscape.client.coinbase.model.OrderBook
import app.lemley.crypscape.extensions.app.toDecimalFormat
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import java.text.DecimalFormat


class DepthXAxisFormatter constructor(
    private val depth: OrderBook.Depth
) : IAxisValueFormatter {

    private val formatter = DecimalFormat("#,##0.00#")

    override fun getFormattedValue(value: Float, axis: AxisBase?): String {
        val position = value.toInt()
        val numBids = depth.bids.size
        return if (position > numBids && position < depth.bids.size + depth.asks.size) {
            formatter.format(depth.asks.values.toList()[position - numBids].price)
        } else if (position in 1 until numBids) {
            formatter.format(depth.bids.values.toList()[position].price)
        } else {
            ""
        }
    }

}