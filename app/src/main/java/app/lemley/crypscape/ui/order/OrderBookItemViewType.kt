package app.lemley.crypscape.ui.order

import androidx.annotation.LayoutRes
import app.lemley.crypscape.R
import app.lemley.crypscape.ui.base.IViewType

sealed class OrderBookItemViewType : IViewType {
    companion object {

        fun typeFromId(id: Int): OrderBookItemViewType = when (id) {
            Header.id -> Header
            Ask.id -> Ask
            Spread.id -> Spread
            Bid.id -> Bid
            else -> throw IllegalArgumentException("$id id specified is not valid.  Expected 0..3")
        }
    }

    object Header : OrderBookItemViewType() {
        override val id: Int = 0

        @get:LayoutRes
        override val layoutId = R.layout.item_order_book_header
    }

    object Ask : OrderBookItemViewType() {
        override val id: Int = 1

        @get:LayoutRes
        override val layoutId = R.layout.item_order_book_ask
    }

    object Spread : OrderBookItemViewType() {
        override val id: Int = 2

        @get:LayoutRes
        override val layoutId = R.layout.item_order_book_spread
    }

    object Bid : OrderBookItemViewType() {
        override val id: Int = 3

        @get:LayoutRes
        override val layoutId = R.layout.item_order_book_bid
    }
}