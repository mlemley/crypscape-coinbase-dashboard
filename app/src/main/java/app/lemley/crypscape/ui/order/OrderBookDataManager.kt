package app.lemley.crypscape.ui.order

import android.view.ViewGroup
import androidx.annotation.VisibleForTesting
import app.lemley.crypscape.client.coinbase.model.OrderBook
import app.lemley.crypscape.client.coinbase.model.mergeChanges
import app.lemley.crypscape.extensions.app.inflate
import app.lemley.crypscape.extensions.exhaustive

class OrderBookDataManager {

    companion object {
        const val headerSlots: Int = 1
        const val spreadSlots: Int = 1
        const val maxStackSize: Int = 20
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    var fullOrderBook: OrderBook.SnapShot? = null

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    var orderBook: OrderBook.SnapShot? = null

    var spreadPosition = 1

    private val headerPosition = 0

    val size: Int
        get() = headerSlots + spreadSlots + (orderBook?.asks?.size ?: 0) + (orderBook?.bids?.size
            ?: 0)

    fun updateData(orderBook: OrderBook) {
        when (orderBook) {
            is OrderBook.SnapShot -> updateData(orderBook)
            is OrderBook.L2Update -> updateData(orderBook)
        }.exhaustive

        spreadPosition = 1 + (this.orderBook?.asks?.size ?: 0)
    }

    fun typeForPosition(position: Int): OrderBookItemViewType = when {
        position == headerPosition -> OrderBookItemViewType.Header
        position < spreadPosition -> OrderBookItemViewType.Ask
        position == spreadPosition -> OrderBookItemViewType.Spread
        else -> OrderBookItemViewType.Bid
    }

    private fun updateData(snapshot: OrderBook.SnapShot) {
        fullOrderBook = snapshot
        reduceBook()
    }


    private fun updateData(update: OrderBook.L2Update) {
        orderBook?.let {
            fullOrderBook = it.mergeChanges(update)
            reduceBook()
        }
    }

    // This should be done by view model
    private fun reduceBook() {

        fullOrderBook?.let { fullBook ->
            val asksToRemove: List<Double> = if (fullBook.asks.size > maxStackSize) {
                var count = 0
                val asksToRemove: MutableList<Double> = mutableListOf()
                fullBook.asks.values.reversed().forEach {
                    if (count >= maxStackSize) {
                        asksToRemove.add(it.price)
                    } else {
                        if (it.size > 0) {
                            count += 1
                        } else {
                            asksToRemove.add(it.price)
                        }
                    }
                }
                asksToRemove
            } else {
                emptyList()
            }
            val bidsToRemove: List<Double> = if (fullBook.bids.size > maxStackSize) {
                var count = 0
                val bidsToRemove: MutableList<Double> = mutableListOf()
                fullBook.bids.values.forEach {
                    if (count >= maxStackSize) {
                        bidsToRemove.add(it.price)
                    } else {
                        if (it.size > 0) {
                            count += 1
                        } else {
                            bidsToRemove.add(it.price)
                        }
                    }
                }
                bidsToRemove
            } else {
                emptyList()
            }
            orderBook = fullBook.copy(
                asks = fullBook.asks - asksToRemove,
                bids = fullBook.bids - bidsToRemove
            )
        }
    }

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

    fun bindToHolder(viewHolder: OrderBookViewItemHolder, position: Int) {
        orderBook?.let { book ->
            when (viewHolder) {
                is OrderBookViewItemHolder.HeaderViewHolder -> viewHolder.bindCurrencySet(book.productId)
                is OrderBookViewItemHolder.AskViewHolder -> book.askAtPosition(position - headerSlots)
                    ?.let {
                        viewHolder.bind(
                            it
                        )
                    }
                is OrderBookViewItemHolder.BidViewHolder -> book.bidAtPosition(position - headerSlots - spreadSlots - book.asks.size)
                    ?.let {
                        viewHolder.bind(
                            it
                        )
                    }
                is OrderBookViewItemHolder.SpreadViewHolder -> viewHolder.bind(
                    book.productId,
                    book.spread
                )
            }.exhaustive
        }
    }
}
