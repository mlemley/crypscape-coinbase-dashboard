package app.lemley.crypscape.ui.order

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import app.lemley.crypscape.client.coinbase.model.OrderBook
import app.lemley.crypscape.client.coinbase.model.Subscribe
import app.lemley.crypscape.repository.CoinBaseRealTimeRepository
import app.lemley.crypscape.repository.DefaultMarketDataRepository
import app.lemley.crypscape.rules.TestContextProvider
import app.lemley.crypscape.rules.TestCoroutineRule
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test

@FlowPreview
@ExperimentalCoroutinesApi
class OrderBookViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    private fun createViewModel(
        coinBaseRealTimeRepository: CoinBaseRealTimeRepository = mockk(relaxed = true),
        defaultMarketDataRepository: DefaultMarketDataRepository = mockk {
            every { loadDefault() } returns mockk {
                every { productRemoteId } returns "BTC-USD"
            }
        }

    ): OrderBookViewModel {
        return OrderBookViewModel(
            coinBaseRealTimeRepository,
            TestContextProvider(),
            defaultMarketDataRepository
        )
    }

    @Test
    fun fetches_and_subscribes_to_level2_for_product() =
        testCoroutineRule.runBlockingTest {
            val coinBaseRealTimeRepository: CoinBaseRealTimeRepository = mockk(relaxed = true)
            val viewModel = createViewModel(coinBaseRealTimeRepository = coinBaseRealTimeRepository)

            assertThat(viewModel.productId).isEqualTo("BTC-USD")

            verify {
                runBlocking {
                    coinBaseRealTimeRepository.subscribe(
                        listOf("BTC-USD"),
                        listOf(Subscribe.Channel.Level2)
                    )
                }
            }
        }

    @Test
    fun updates_with_order_book() = testCoroutineRule.runBlockingTest {
        val orderBook = mockk<OrderBook>(relaxed = true) {
            every { productId } returns "BTC-USD"
        }
        val coinBaseRealTimeRepository: CoinBaseRealTimeRepository = mockk(relaxed = true) {
            every { orderBookFlow } returns flow {
                emit(orderBook)
            }
        }
        val viewModel = createViewModel(coinBaseRealTimeRepository = coinBaseRealTimeRepository)
        val observer = mockk<Observer<OrderBook>>(relaxed = true)

        viewModel.orderBookState.observeForever(observer)

        verify {
            observer.onChanged(orderBook)
        }

    }
}