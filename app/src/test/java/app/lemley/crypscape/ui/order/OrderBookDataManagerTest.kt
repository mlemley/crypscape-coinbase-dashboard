package app.lemley.crypscape.ui.order

import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.lemley.crypscape.R
import app.lemley.crypscape.app.TestCrypScapeApplication
import app.lemley.crypscape.client.coinbase.model.*
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class OrderBookDataManagerTest {
    companion object {
        val snapshot = OrderBook.SnapShot(
            productId = "BTC-USD",
            asks = mapOf(
                Pair(10202.55, Ask(10202.55, 0.57753524))
            ),
            bids = mapOf(
                Pair(10101.10, Bid(10101.10, 0.45054140))
            )
        )
    }

    private fun createManager(): OrderBookDataManager = OrderBookDataManager()
    private val parent: ViewGroup
        get() = LinearLayout(ApplicationProvider.getApplicationContext<TestCrypScapeApplication>())

    @Test
    fun updates_order_book_from_snapshot() {
        val manager = createManager()

        manager.updateData(snapshot)

        assertThat(manager.orderBook).isEqualTo(snapshot)
    }

    @Test
    fun updates_order_book_from_update() {
        val update = OrderBook.L2Update(
            productId = "BTC-USD",
            time = "2019-08-14T20:42:27.265Z",
            changes = listOf(
                Change(
                    Side.Sell,
                    10202.8,
                    0.262567
                ),
                Change(
                    Side.Sell,
                    10202.55,
                    0.0
                ),
                Change(
                    Side.Buy,
                    10101.8,
                    0.162567
                ),
                Change(
                    Side.Buy,
                    10101.10,
                    0.1
                )
            )
        )

        val manager = createManager()
        manager.updateData(snapshot)

        manager.updateData(update)

        assertThat(manager.orderBook).isEqualTo(
            snapshot.copy(
                asks = mapOf(
                    Pair(10202.8, Ask(10202.8, 0.262567, changed=true)),
                    Pair(10202.55, Ask(10202.55, 0.0, changed = true))
                ),
                bids = mapOf(
                    Pair(10101.8, Bid(10101.8, 0.162567, changed = true)),
                    Pair(10101.10, Bid(10101.10, 0.1, changed = true))
                )
            )
        )
    }

    @Test
    fun returns_calculated_size() {
        val manager = createManager()

        manager.updateData(snapshot)

        assertThat(manager.size).isEqualTo(4)
    }

    @Test
    fun calculates_spread_position() {
        val manager = createManager()

        manager.updateData(snapshot)

        assertThat(manager.spreadPosition).isEqualTo(2)
    }

    @Test
    fun returns_type_for_position() {
        val manager = createManager()

        manager.updateData(snapshot)

        assertThat(manager.typeForPosition(0)).isEqualTo(OrderBookItemViewType.Header)
        assertThat(manager.typeForPosition(1)).isEqualTo(OrderBookItemViewType.Ask)
        assertThat(manager.typeForPosition(2)).isEqualTo(OrderBookItemViewType.Spread)
        assertThat(manager.typeForPosition(3)).isEqualTo(OrderBookItemViewType.Bid)
    }

    @Test
    fun creates_view_holder__for_header() {
        val manager = createManager()

        val viewHolder = manager.holderForType(OrderBookItemViewType.Header, parent)

        assertThat(viewHolder).isInstanceOf(OrderBookViewItemHolder.HeaderViewHolder::class.java)
        assertThat(viewHolder.itemView.findViewById<TextView>(R.id.market_size)).isNotNull()
        assertThat(viewHolder.itemView.findViewById<TextView>(R.id.price)).isNotNull()
        assertThat(viewHolder.itemView.findViewById<TextView>(R.id.my_size)).isNotNull()
    }

    @Test
    fun creates_view_holder__for_ask() {
        val manager = createManager()

        val viewHolder = manager.holderForType(OrderBookItemViewType.Ask, parent)

        assertThat(viewHolder).isInstanceOf(OrderBookViewItemHolder.AskViewHolder::class.java)
        assertThat(viewHolder.itemView.findViewById<TextView>(R.id.market_size)).isNotNull()
        assertThat(viewHolder.itemView.findViewById<TextView>(R.id.price)).isNotNull()
        assertThat(viewHolder.itemView.findViewById<TextView>(R.id.my_size)).isNotNull()
    }

    @Test
    fun creates_view_holder__for_spread() {
        val manager = createManager()

        val viewHolder = manager.holderForType(OrderBookItemViewType.Spread, parent)

        assertThat(viewHolder).isInstanceOf(OrderBookViewItemHolder.SpreadViewHolder::class.java)
        assertThat(viewHolder.itemView.findViewById<TextView>(R.id.spread_lbl)).isNotNull()
        assertThat(viewHolder.itemView.findViewById<TextView>(R.id.spread_value)).isNotNull()
    }

    @Test
    fun creates_view_holder__for_bid() {
        val manager = createManager()

        val viewHolder = manager.holderForType(OrderBookItemViewType.Bid, parent)

        assertThat(viewHolder).isInstanceOf(OrderBookViewItemHolder.BidViewHolder::class.java)
        assertThat(viewHolder.itemView.findViewById<TextView>(R.id.market_size)).isNotNull()
        assertThat(viewHolder.itemView.findViewById<TextView>(R.id.price)).isNotNull()
        assertThat(viewHolder.itemView.findViewById<TextView>(R.id.my_size)).isNotNull()
    }
}
