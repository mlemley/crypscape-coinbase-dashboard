package app.lemley.crypscape.repository

import app.lemley.crypscape.model.MarketConfiguration
import app.lemley.crypscape.persistance.entities.Candle
import app.lemley.crypscape.persistance.entities.Granularity
import app.lemley.crypscape.persistance.entities.Product
import com.google.common.truth.Truth.assertThat
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verifyOrder
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Test

@ExperimentalCoroutinesApi
class CoinBaseRepositoryTest {

    private fun createRepository(
        coinBaseCurrencyRepository: CoinBaseCurrencyRepository = mockk(relaxUnitFun = true),
        coinBaseProductRepository: CoinBaseProductRepository = mockk(relaxUnitFun = true),
        coinBaseCandleRepository: CoinBaseCandleRepository = mockk(relaxUnitFun = true)

    ): CoinBaseRepository = CoinBaseRepository(
        currencyRepository = coinBaseCurrencyRepository,
        productRepository = coinBaseProductRepository,
        candleRepository = coinBaseCandleRepository
    )

    @Test
    fun syncs_products_sync___both_currencies_and_products() {
        val currencyRepository: CoinBaseCurrencyRepository = mockk(relaxUnitFun = true)
        val productRepository: CoinBaseProductRepository = mockk(relaxUnitFun = true)
        val repository = createRepository(currencyRepository, productRepository)

        runBlocking {
            repository.syncProducts()
        }

        verifyOrder {
            runBlocking {
                currencyRepository.sync()
                productRepository.sync()
            }
        }

        confirmVerified(currencyRepository, productRepository)
    }

    @Test
    fun fetch_candles_for_market_configuration() {
        val product = Product(
            platformId = 1,
            id = 2,
            serverId = "BTC-USD",
            quoteCurrency = 3,
            baseCurrency = 4
        )
        val granularity = Granularity.Hour
        val configuration = MarketConfiguration(product, granularity)
        val candles = flowOf<List<Candle>>(mockk(), mockk())
        val candleRepository: CoinBaseCandleRepository = mockk {
            every { runBlocking { candlesFor(configuration) } } returns candles
        }
        val repository = createRepository(coinBaseCandleRepository = candleRepository)

        runBlocking {
            assertThat(repository.candlesForConfiguration(configuration)).isEqualTo(candles)
        }
    }
}