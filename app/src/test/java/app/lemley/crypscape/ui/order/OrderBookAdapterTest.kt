package app.lemley.crypscape.ui.order

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.lemley.crypscape.client.coinbase.model.OrderBook
import com.google.common.truth.Truth.assertThat
import io.mockk.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class OrderBookAdapterTest {


    private fun createAdapter(
        orderBookDataManager: OrderBookDataManager = mockk(relaxed = true)
    ): OrderBookAdapter = OrderBookAdapter(
        orderBookDataManager
    )

    @Test
    fun update_with_delegates_to_order_book_data_manager_then_invalidates() {
        val orderBook: OrderBook = mockk(relaxed = true)
        val orderBookDataManager: OrderBookDataManager = mockk(relaxed = true)
        val observer: RecyclerView.AdapterDataObserver = mockk(relaxed = true)
        val adapter = createAdapter(orderBookDataManager)
        adapter.registerAdapterDataObserver(observer)

        adapter.updateWith(orderBook)

        verifyOrder {
            orderBookDataManager.updateData(orderBook)
            observer.onChanged()
        }

        confirmVerified(orderBookDataManager, observer)
    }

    @Test
    fun returns_item_count_from_manager() {
        val orderBookDataManager: OrderBookDataManager = mockk {
            every { size } returns 12
        }

        assertThat(createAdapter(orderBookDataManager).itemCount).isEqualTo(12)
    }

    @Test
    fun view_type_for_given_position_is_delegated() {
        val orderBookDataManager: OrderBookDataManager = mockk {
            every { typeForPosition(0) } returns OrderBookItemViewType.Header
            every { typeForPosition(1) } returns OrderBookItemViewType.Ask
            every { typeForPosition(2) } returns OrderBookItemViewType.Ask
            every { typeForPosition(3) } returns OrderBookItemViewType.Spread
            every { typeForPosition(4) } returns OrderBookItemViewType.Bid
            every { typeForPosition(5) } returns OrderBookItemViewType.Bid
        }

        val adapter = createAdapter(orderBookDataManager)
        assertThat(adapter.getItemViewType(0)).isEqualTo(OrderBookItemViewType.Header.id)
        assertThat(adapter.getItemViewType(1)).isEqualTo(OrderBookItemViewType.Ask.id)
        assertThat(adapter.getItemViewType(2)).isEqualTo(OrderBookItemViewType.Ask.id)
        assertThat(adapter.getItemViewType(3)).isEqualTo(OrderBookItemViewType.Spread.id)
        assertThat(adapter.getItemViewType(4)).isEqualTo(OrderBookItemViewType.Bid.id)
        assertThat(adapter.getItemViewType(5)).isEqualTo(OrderBookItemViewType.Bid.id)
    }

    @Test
    fun creates_view_holder_for_view_type() {
        val parent: ViewGroup = mockk()
        val viewHolder = mockk<OrderBookViewItemHolder.HeaderViewHolder>()
        val orderBookDataManager: OrderBookDataManager = mockk {
            every {
                holderForType(
                    OrderBookItemViewType.Header,
                    parent
                )
            } returns viewHolder
        }

        assertThat(createAdapter(orderBookDataManager).onCreateViewHolder(parent, 0)).isEqualTo(
            viewHolder
        )
    }

    @Test
    fun binds_view_holder_to_data_at_position() {
        val viewHolder = mockk<OrderBookViewItemHolder.HeaderViewHolder>()
        val position = 25
        val orderBookDataManager: OrderBookDataManager = mockk(relaxUnitFun = true)

        createAdapter(orderBookDataManager).onBindViewHolder(viewHolder, position)

        verify {
            orderBookDataManager.bindToHolder(viewHolder, position)
        }
    }
}