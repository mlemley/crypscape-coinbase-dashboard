package app.lemley.crypscape.ui.order

import android.view.ViewGroup
import androidx.annotation.VisibleForTesting
import androidx.recyclerview.widget.RecyclerView
import app.lemley.crypscape.client.coinbase.model.OrderBook
import app.lemley.crypscape.extensions.app.inflate
import app.lemley.crypscape.extensions.exhaustive

class OrderBookAdapter : RecyclerView.Adapter<OrderBookViewItemHolder>() {

    companion object {
        const val headerSlots: Int = 1
        const val spreadSlots: Int = 1
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    var orderBook: OrderBook.SnapShot? = null

    var spreadPosition = 1

    private val headerPosition = 0

    fun updateWith(snapshot: OrderBook.SnapShot) {
        this.orderBook = snapshot
        spreadPosition = headerSlots + (this.orderBook?.asks?.size ?: 0)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int =
        headerSlots + spreadSlots + (orderBook?.asks?.size ?: 0) + (orderBook?.bids?.size
            ?: 0)

    override fun getItemViewType(position: Int): Int = when {
        position == headerPosition -> OrderBookItemViewType.Header.id
        position < spreadPosition -> OrderBookItemViewType.Ask.id
        position == spreadPosition -> OrderBookItemViewType.Spread.id
        else -> OrderBookItemViewType.Bid.id
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderBookViewItemHolder {
        val orderBookItemViewType = OrderBookItemViewType.typeFromId(viewType)
        return holderForType(orderBookItemViewType, parent)
    }

    override fun onBindViewHolder(holder: OrderBookViewItemHolder, position: Int) {
        orderBook?.let { book ->
            when (holder) {
                is OrderBookViewItemHolder.HeaderViewHolder -> holder.bindCurrencySet(book.productId)
                is OrderBookViewItemHolder.AskViewHolder -> book.askAtPosition(position - headerSlots)
                    ?.let {
                        holder.bind(
                            it
                        )
                    }
                is OrderBookViewItemHolder.BidViewHolder -> book.bidAtPosition(position - headerSlots - spreadSlots - book.asks.size)
                    ?.let {
                        holder.bind(
                            it
                        )
                    }
                is OrderBookViewItemHolder.SpreadViewHolder -> holder.bind(
                    book.productId,
                    book.spread
                )
            }.exhaustive
        }
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun holderForType(
        viewType: OrderBookItemViewType,
        parent: ViewGroup
    ): OrderBookViewItemHolder {
        val view = parent.inflate(viewType.layoutId)
        return when (viewType) {
            is OrderBookItemViewType.Header -> OrderBookViewItemHolder.HeaderViewHolder(view)
            is OrderBookItemViewType.Ask -> OrderBookViewItemHolder.AskViewHolder(view)
            is OrderBookItemViewType.Spread -> OrderBookViewItemHolder.SpreadViewHolder(view)
            is OrderBookItemViewType.Bid -> OrderBookViewItemHolder.BidViewHolder(view)
        }.exhaustive
    }
}