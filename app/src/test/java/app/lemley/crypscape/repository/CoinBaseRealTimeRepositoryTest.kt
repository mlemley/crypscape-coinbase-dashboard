package app.lemley.crypscape.repository

import app.lemley.crypscape.client.coinbase.CoinBaseWSService
import app.lemley.crypscape.client.coinbase.model.Subscribe
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test

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


}