package app.lemley.crypscape.ui.book

import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.lemley.crypscape.R
import app.lemley.crypscape.client.coinbase.model.Ask
import app.lemley.crypscape.client.coinbase.model.Bid
import app.lemley.crypscape.client.coinbase.model.OrderBook
import app.lemley.crypscape.client.coinbase.model.OrderBookType
import com.google.common.truth.Truth.assertThat
import io.mockk.confirmVerified
import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifyOrder
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class OrderBookAdapterTest {

    private fun createAdapter(): OrderBookAdapter = OrderBookAdapter()
    private val parent: ViewGroup = LinearLayout(ApplicationProvider.getApplicationContext())

    @Test
    fun update_with_snapshot_replaces_order_book__then_invalidates() {
        val orderBook: OrderBook.SnapShot = mockk(relaxed = true)
        val observer: RecyclerView.AdapterDataObserver = mockk(relaxed = true)
        val adapter = createAdapter()
        adapter.registerAdapterDataObserver(observer)

        adapter.updateWith(orderBook)

        verifyOrder {
            observer.onChanged()
        }

        confirmVerified(observer)
        assertThat(adapter.orderBook).isEqualTo(orderBook)
    }

    @Test
    fun returns_item_count() {
        val adapter = createAdapter()

        adapter.orderBook = OrderBook.SnapShot(
            productId = "BTC-USD",
            type = OrderBookType.SnapShot,
            asks = mapOf(
                2.0 to Ask(2.0, 20.1),
                2.1 to Ask(2.1, 21.0)
            ),
            bids = mapOf(
                1.9 to Bid(1.9, 19.0),
                1.8 to Bid(1.8, 18.0),
                1.7 to Bid(1.7, 17.0)
            )
        )

        assertThat(adapter.itemCount).isEqualTo(7)
    }

    @Test
    fun view_type_for_given_position() {
        val adapter = createAdapter()

        adapter.updateWith(
            OrderBook.SnapShot(
                productId = "BTC-USD",
                type = OrderBookType.SnapShot,
                asks = mapOf(
                    2.0 to Ask(2.0, 20.1),
                    2.1 to Ask(2.1, 21.0)
                ),
                bids = mapOf(
                    1.9 to Bid(1.9, 19.0),
                    1.8 to Bid(1.8, 18.0),
                    1.7 to Bid(1.7, 17.0)
                )
            )
        )

        assertThat(adapter.getItemViewType(0)).isEqualTo(OrderBookItemViewType.Header.id)
        assertThat(adapter.getItemViewType(1)).isEqualTo(OrderBookItemViewType.Ask.id)
        assertThat(adapter.getItemViewType(2)).isEqualTo(OrderBookItemViewType.Ask.id)
        assertThat(adapter.getItemViewType(3)).isEqualTo(OrderBookItemViewType.Spread.id)
        assertThat(adapter.getItemViewType(4)).isEqualTo(OrderBookItemViewType.Bid.id)
        assertThat(adapter.getItemViewType(5)).isEqualTo(OrderBookItemViewType.Bid.id)
        assertThat(adapter.getItemViewType(6)).isEqualTo(OrderBookItemViewType.Bid.id)
    }

    @Test
    fun creates_view_holder_for_view_type() {
        val adapter = createAdapter()


        adapter.updateWith(
            OrderBook.SnapShot(
                productId = "BTC-USD",
                type = OrderBookType.SnapShot,
                asks = mapOf(
                    2.0 to Ask(2.0, 20.1),
                    2.1 to Ask(2.1, 21.0)
                ),
                bids = mapOf(
                    1.9 to Bid(1.9, 19.0),
                    1.8 to Bid(1.8, 18.0),
                    1.7 to Bid(1.7, 17.0)
                )
            )
        )

        with(parent) {
            assertThat(adapter.onCreateViewHolder(this, 0)).isInstanceOf(OrderBookViewItemHolder.HeaderViewHolder::class.java)
            assertThat(adapter.onCreateViewHolder(this, 1)).isInstanceOf(OrderBookViewItemHolder.AskViewHolder::class.java)
            assertThat(adapter.onCreateViewHolder(this, 2)).isInstanceOf(OrderBookViewItemHolder.SpreadViewHolder::class.java)
            assertThat(adapter.onCreateViewHolder(this, 3)).isInstanceOf(OrderBookViewItemHolder.BidViewHolder::class.java)
        }
    }

    @Test
    fun binds_view_holder_to_data_at_position() {
        val adapter = createAdapter()
        val holder = mockk<OrderBookViewItemHolder.BidViewHolder>(relaxUnitFun = true)
        val bid = Bid(1.7, 17.0)

        adapter.updateWith(
            OrderBook.SnapShot(
                productId = "BTC-USD",
                type = OrderBookType.SnapShot,
                asks = mapOf(
                    2.0 to Ask(2.0, 20.1),
                    2.1 to Ask(2.1, 21.0)
                ),
                bids = mapOf(
                    1.9 to Bid(1.9, 19.0),
                    1.8 to Bid(1.8, 18.0),
                    1.7 to bid
                )
            )
        )

        adapter.onBindViewHolder(holder, 6)

        verify{
            holder.bind(bid)
        }
    }

    @Test
    fun returns_type_for_position() {
        val adapter = createAdapter()

        adapter.updateWith(
            OrderBook.SnapShot(
                productId = "BTC-USD",
                type = OrderBookType.SnapShot,
                asks = mapOf(
                    2.0 to Ask(2.0, 20.1),
                    2.1 to Ask(2.1, 21.0)
                ),
                bids = mapOf(
                    1.9 to Bid(1.9, 19.0),
                    1.8 to Bid(1.8, 18.0),
                    1.7 to Bid(1.7, 17.0)
                )
            )
        )

        assertThat(adapter.getItemViewType(0)).isEqualTo(OrderBookItemViewType.Header.id)
        assertThat(adapter.getItemViewType(1)).isEqualTo(OrderBookItemViewType.Ask.id)
        assertThat(adapter.getItemViewType(2)).isEqualTo(OrderBookItemViewType.Ask.id)
        assertThat(adapter.getItemViewType(3)).isEqualTo(OrderBookItemViewType.Spread.id)
        assertThat(adapter.getItemViewType(4)).isEqualTo(OrderBookItemViewType.Bid.id)
        assertThat(adapter.getItemViewType(5)).isEqualTo(OrderBookItemViewType.Bid.id)
        assertThat(adapter.getItemViewType(6)).isEqualTo(OrderBookItemViewType.Bid.id)
    }

    @Test
    fun creates_view_holder__for_header() {
        val adapter = createAdapter()

        val viewHolder = adapter.holderForType(OrderBookItemViewType.Header, parent)

        assertThat(viewHolder).isInstanceOf(OrderBookViewItemHolder.HeaderViewHolder::class.java)
        assertThat(viewHolder.itemView.findViewById<TextView>(R.id.market_size)).isNotNull()
        assertThat(viewHolder.itemView.findViewById<TextView>(R.id.price)).isNotNull()
        assertThat(viewHolder.itemView.findViewById<TextView>(R.id.my_size)).isNotNull()
    }

    @Test
    fun creates_view_holder__for_ask() {
        val adapter = createAdapter()

        val viewHolder = adapter.holderForType(OrderBookItemViewType.Ask, parent)

        assertThat(viewHolder).isInstanceOf(OrderBookViewItemHolder.AskViewHolder::class.java)
        assertThat(viewHolder.itemView.findViewById<TextView>(R.id.market_size)).isNotNull()
        assertThat(viewHolder.itemView.findViewById<TextView>(R.id.price)).isNotNull()
        assertThat(viewHolder.itemView.findViewById<TextView>(R.id.my_size)).isNotNull()
    }

    @Test
    fun creates_view_holder__for_spread() {
        val adapter = createAdapter()

        val viewHolder = adapter.holderForType(OrderBookItemViewType.Spread, parent)

        assertThat(viewHolder).isInstanceOf(OrderBookViewItemHolder.SpreadViewHolder::class.java)
        assertThat(viewHolder.itemView.findViewById<TextView>(R.id.spread_lbl)).isNotNull()
        assertThat(viewHolder.itemView.findViewById<TextView>(R.id.spread_value)).isNotNull()
    }

    @Test
    fun creates_view_holder__for_bid() {
        val adapter = createAdapter()

        val viewHolder = adapter.holderForType(OrderBookItemViewType.Bid, parent)

        assertThat(viewHolder).isInstanceOf(OrderBookViewItemHolder.BidViewHolder::class.java)
        assertThat(viewHolder.itemView.findViewById<TextView>(R.id.market_size)).isNotNull()
        assertThat(viewHolder.itemView.findViewById<TextView>(R.id.price)).isNotNull()
        assertThat(viewHolder.itemView.findViewById<TextView>(R.id.my_size)).isNotNull()
    }

    @Test
    fun empty_when_ask_and_bids_are_0() {
        val adapter = createAdapter()
        assertThat(adapter.isEmpty()).isTrue()

        adapter.updateWith(
            OrderBook.SnapShot(
                productId = "BTC-USD",
                type = OrderBookType.SnapShot,
                asks = mapOf(
                    2.0 to Ask(2.0, 20.1),
                    2.1 to Ask(2.1, 21.0)
                ),
                bids = mapOf(
                    1.9 to Bid(1.9, 19.0),
                    1.8 to Bid(1.8, 18.0),
                    1.7 to Bid(1.7, 17.0)
                )
            )
        )
        assertThat(adapter.isEmpty()).isFalse()
    }
}