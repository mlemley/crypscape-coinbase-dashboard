package app.lemley.crypscape.ui.order

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import app.lemley.crypscape.R
import app.lemley.crypscape.client.coinbase.model.Ask
import app.lemley.crypscape.client.coinbase.model.Bid
import java.text.DecimalFormat

const val sizeFormat: String = "#,###.0000"
const val currencyFormat: String = "#,###.00"

sealed class OrderBookViewItemHolder(view: View) : RecyclerView.ViewHolder(view) {

    class HeaderViewHolder(view: View) : OrderBookViewItemHolder(view) {
        fun bindCurrencySet(productId: String) {
            val quoteCurrency = productId.split("-")[1]
            itemView.findViewById<TextView>(R.id.price).text =
                itemView.context.getString(R.string.order_book_price, quoteCurrency)
        }
    }

    class AskViewHolder(view: View) : OrderBookViewItemHolder(view) {
        fun bind(ask: Ask) {
            itemView.findViewById<TextView>(R.id.price).text =
                DecimalFormat(currencyFormat).format(ask.price)
            itemView.findViewById<TextView>(R.id.market_size).text =
                DecimalFormat(sizeFormat).format(ask.size)
            itemView.findViewById<TextView>(R.id.my_size).text = "-"
        }
    }

    class BidViewHolder(view: View) : OrderBookViewItemHolder(view) {
        fun bind(bid: Bid) {
            itemView.findViewById<TextView>(R.id.price).text =
                DecimalFormat(currencyFormat).format(bid.price)
            itemView.findViewById<TextView>(R.id.market_size).text =
                DecimalFormat(sizeFormat).format(bid.size)
            itemView.findViewById<TextView>(R.id.my_size).text = "-"
            itemView.layoutParams = RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
    }

    class SpreadViewHolder(view: View) : OrderBookViewItemHolder(view) {
        fun bind(productId: String, spread: Double) {
            val quoteCurrency = productId.split("-")[1]
            itemView.findViewById<TextView>(R.id.spread_lbl).text =
                itemView.context.getString(R.string.order_book_spread_label, quoteCurrency)
            itemView.findViewById<TextView>(R.id.spread_value).text = DecimalFormat(
                currencyFormat
            ).format(spread)
        }
    }
}