package app.lemley.crypscape.repository

import app.lemley.crypscape.client.coinbase.model.Ticker
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
        coinBaseCandleRepository: CoinBaseCandleRepository = mockk(relaxUnitFun = true),
        coinBaseTickerRepository: CoinBaseTickerRepository = mockk(relaxUnitFun = true)

    ): CoinBaseRepository = CoinBaseRepository(
        currencyRepository = coinBaseCurrencyRepository,
        productRepository = coinBaseProductRepository,
        candleRepository = coinBaseCandleRepository,
        tickerRepository = coinBaseTickerRepository
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
        val granularity = Granularity.Hour
        val configuration = MarketConfiguration(platformId = 1, productRemoteId = "BTC-USD", granularity = granularity)
        val candles = flowOf<List<Candle>>(mockk(), mockk())
        val candleRepository: CoinBaseCandleRepository = mockk {
            every { runBlocking { candlesFor(configuration) } } returns candles
        }
        val repository = createRepository(coinBaseCandleRepository = candleRepository)

        runBlocking {
            assertThat(repository.candlesForConfiguration(configuration)).isEqualTo(candles)
        }
    }

    @Test
    fun proxies_ticker_fetch() {
        val remoteId = "BTC-USD"
        val platformId:Long = 1

        val ticker: Ticker = mockk(relaxUnitFun = true)

        val coinBaseTickerRepository: CoinBaseTickerRepository = mockk {
            every { runBlocking { tickerFor(remoteId) } } returns ticker
        }

        val repository = createRepository(coinBaseTickerRepository = coinBaseTickerRepository)


        var actual: Ticker? = null
        runBlocking {
            actual = repository.tickerForConfiguration(
                MarketConfiguration(
                    platformId,
                    remoteId,
                    Granularity.FifteenMinutes
                )
            )
        }

        assertThat(actual).isEqualTo(ticker)
    }
}