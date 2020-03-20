package app.lemley.crypscape.ui.book

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import app.lemley.crypscape.R
import app.lemley.crypscape.client.coinbase.model.Ask
import app.lemley.crypscape.client.coinbase.model.Bid
import java.text.DecimalFormat

const val sizeFormat: String = "#,###.0000"
const val currencyFormat: String = "#,##0.00"

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
            itemView.background = null
            val price = itemView.findViewById<TextView>(R.id.price)
            val size = itemView.findViewById<TextView>(R.id.market_size)
            val my_size = itemView.findViewById<TextView>(R.id.my_size)

            price.text = DecimalFormat(currencyFormat).format(ask.price)
            price.setTextAppearance(R.style.TextAppearance_OrderBook_Ask)
            size.text = DecimalFormat(sizeFormat).format(
                if (ask.size == 0.0) ask.historicalSize
                else ask.size
            )
            size.setTextAppearance(R.style.TextAppearance)
            my_size.text = "-"
            my_size.setTextAppearance(R.style.TextAppearance)

            if (ask.size == 0.0) {
                itemView.background =
                    itemView.context.getDrawable(R.drawable.background_order_book_zero_size)
                price.setTextAppearance(R.style.TextAppearance_OrderBook_Disabled)
                size.setTextAppearance(R.style.TextAppearance_OrderBook_Disabled)
                my_size.setTextAppearance(R.style.TextAppearance_OrderBook_Disabled)
            } else if (ask.changed) {
                itemView.background =
                    itemView.context.getDrawable(R.drawable.background_order_book_ask_change)
            }
        }
    }

    class BidViewHolder(view: View) : OrderBookViewItemHolder(view) {
        fun bind(bid: Bid) {
            itemView.background = null
            val price = itemView.findViewById<TextView>(R.id.price)
            val size = itemView.findViewById<TextView>(R.id.market_size)
            val my_size = itemView.findViewById<TextView>(R.id.my_size)

            price.text = DecimalFormat(currencyFormat).format(bid.price)
            price.setTextAppearance(R.style.TextAppearance_OrderBook_Bid)
            size.text = DecimalFormat(sizeFormat).format(
                if (bid.size == 0.0) bid.historicalSize
                else bid.size
            )
            size.setTextAppearance(R.style.TextAppearance)
            my_size.text = "-"
            my_size.setTextAppearance(R.style.TextAppearance)

            if (bid.size == 0.0) {
                itemView.background =
                    itemView.context.getDrawable(R.drawable.background_order_book_zero_size)
                price.setTextAppearance(R.style.TextAppearance_OrderBook_Disabled)
                size.setTextAppearance(R.style.TextAppearance_OrderBook_Disabled)
                my_size.setTextAppearance(R.style.TextAppearance_OrderBook_Disabled)
            } else if (bid.changed) {
                itemView.background =
                    itemView.context.getDrawable(R.drawable.background_order_book_bid_change)
            }
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