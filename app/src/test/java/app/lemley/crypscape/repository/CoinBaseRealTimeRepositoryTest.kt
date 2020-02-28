package app.lemley.crypscape.repository

import app.lemley.crypscape.client.coinbase.CoinBaseWSService
import app.lemley.crypscape.client.coinbase.model.Subscribe
import app.lemley.crypscape.client.coinbase.model.Ticker
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.runBlocking
import org.junit.Ignore
import org.junit.Test
import kotlinx.coroutines.flow.Flow as Flow1

@FlowPreview
@ExperimentalCoroutinesApi
class CoinBaseRealTimeRepositoryTest {

    private fun createRepository(
        coinBaseWSService: CoinBaseWSService = mockk(relaxUnitFun = true)
    ): CoinBaseRealTimeRepository =
        CoinBaseRealTimeRepository(coinBaseWSService)

    // TODO  test without connection subscribes when connection opened

    @Ignore // test with connection immediatlly subscribes
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

        runBlocking {
            repository.subscribe(products, channels)
        }

        verify {
            wsService.sendSubscribe(expectedSubscribe)
        }
    }

    @Ignore // TODO ensure that subscribe unsubscribes from channels first
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

        runBlocking {
            repository.subscribe(products, channels)

            repository.unsubscribe()
        }

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