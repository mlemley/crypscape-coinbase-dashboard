package app.lemley.crypscape.repository

import app.lemley.crypscape.client.coinbase.CoinBaseWSService
import app.lemley.crypscape.client.coinbase.model.Subscribe
import app.lemley.crypscape.client.coinbase.model.Ticker
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.consumeAsFlow
import org.junit.Test
import kotlinx.coroutines.flow.Flow as Flow1

@FlowPreview
class CoinBaseRealTimeRepositoryTest {

    private fun createRepository(
        coinBaseWSService: CoinBaseWSService
    ): CoinBaseRealTimeRepository =
        CoinBaseRealTimeRepository(coinBaseWSService)

    @Test
    fun subscribes_to_topics() {
        val wsService: CoinBaseWSService = mockk(relaxed = true)
        val repository = createRepository(wsService)
        val expectedSubscribe = Subscribe(
            Subscribe.Type.Subscribe.toString(),
            listOf("BTC-USD"),
            listOf(Subscribe.Channel.Ticker.toString())
        )
        val products = listOf("BTC-USD")
        val channels = listOf(Subscribe.Channel.Ticker)

        repository.subscribe(products, channels)

        verify {
            wsService.sendSubscribe(expectedSubscribe)
        }
    }

    @Test
    fun un_subscribes_from_topics() {
        val wsService: CoinBaseWSService = mockk(relaxed = true)
        val repository = createRepository(wsService)
        val expectedSubscribe = Subscribe(
            Subscribe.Type.Unsubscribe.toString(),
            listOf("BTC-USD"),
            listOf(Subscribe.Channel.Ticker.toString())
        )
        val products = listOf("BTC-USD")
        val channels = listOf(Subscribe.Channel.Ticker)

        repository.unsubscribe(products, channels)

        verify {
            wsService.sendSubscribe(expectedSubscribe)
        }
    }

    @Test
    fun provides_access_to_ticker_flow() {
        val receiveChannel:ReceiveChannel<Ticker> = mockk(relaxed = true)
        val result = receiveChannel.consumeAsFlow()

        val wsService: CoinBaseWSService = mockk(relaxed = true) {
            every { observeTicker() } returns receiveChannel
        }
        val repository = createRepository(wsService)

        assertThat(repository.tickerFlow.toString()).isEqualTo(result.toString())
    }
}