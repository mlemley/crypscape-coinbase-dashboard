package app.lemley.crypscape.ui.order

import androidx.fragment.app.testing.FragmentScenario
import androidx.lifecycle.LiveData
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.lemley.crypscape.app.Helpers
import app.lemley.crypscape.client.coinbase.model.OrderBook
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

@FlowPreview
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class OrderBookFragmentTest {

    private fun createScenario(
        adapter: OrderBookAdapter = mockk(relaxed = true),
        liveDataState: LiveData<OrderBook.SnapShot> = mockk(relaxUnitFun = true),
        orderBookViewModel: OrderBookViewModel = mockk(relaxUnitFun = true) {
            every { orderBookState } returns liveDataState
        }
    ): FragmentScenario<OrderBookFragment> {
        Helpers.loadModules(module {
            viewModel { orderBookViewModel }
            single { adapter }
        })
        return FragmentScenario.launch(OrderBookFragment::class.java)
    }

    @Test
    fun observes_order_book_state_changes() {
        val liveDataState: LiveData<OrderBook.SnapShot> = mockk(relaxUnitFun = true)
        val orderBookViewModel: OrderBookViewModel = mockk(relaxUnitFun = true) {
            every { orderBookState } returns liveDataState
        }

        createScenario(orderBookViewModel = orderBookViewModel).onFragment { fragment ->
            verify {
                liveDataState.observe(fragment.viewLifecycleOwner, fragment.orderBookStateObserver)
            }
        }
    }

    @Test
    fun prepares_order_book_list_view() {
        val adapter = mockk<OrderBookAdapter>(relaxed = true)
        createScenario(adapter).onFragment { fragment ->
            assertThat(fragment.binder.orderBook.adapter).isEqualTo(adapter)
            assertThat(fragment.binder.orderBook.hasFixedSize()).isEqualTo(false)
        }
    }

    @Test
    fun updates_adapter_with_new_order_book_state_on_state_change() {
        val adapter = mockk<OrderBookAdapter>(relaxed = true)
        val orderBook:OrderBook.SnapShot = mockk()

        createScenario(adapter).onFragment { fragment ->
            fragment.orderBookStateObserver.onChanged(orderBook)
        }

        verify {
            adapter.updateWith(orderBook)
        }
    }
}