package app.lemley.crypscape.repository

import app.lemley.crypscape.client.coinbase.CoinBaseApi
import app.lemley.crypscape.client.coinbase.model.Ticker
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Test

class CoinBaseTickerRepositoryTest {

    fun createRepository(
        coinBaseApiClient: CoinBaseApi = mockk(relaxUnitFun = true)
    ): CoinBaseTickerRepository = CoinBaseTickerRepository(
        coinBaseApi = coinBaseApiClient
    )

    @Test
    fun fetches_ticker_from_coinbase_for_given_product_id() {
        val productId = "BTC-USD"
        val ticker: Ticker = mockk()
        val apiClient: CoinBaseApi = mockk {
            every { runBlocking { tickerFor(productId) } } returns ticker
        }
        val repository = createRepository(apiClient)

        runBlocking {
            assertThat(repository.tickerFor(productId)).isEqualTo(ticker)
        }
    }


}