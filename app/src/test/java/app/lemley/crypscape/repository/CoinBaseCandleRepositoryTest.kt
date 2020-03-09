package app.lemley.crypscape.repository

import app.lemley.crypscape.client.coinbase.CoinBaseApi
import app.lemley.crypscape.client.coinbase.model.CandleRequest
import app.lemley.crypscape.client.coinbase.model.Ticker
import app.lemley.crypscape.extensions.app.persistance.periodForTickerTime
import app.lemley.crypscape.extensions.app.persistance.previousPeriod
import app.lemley.crypscape.model.MarketConfiguration
import app.lemley.crypscape.persistance.dao.CandleDao
import app.lemley.crypscape.persistance.dao.ProductDao
import app.lemley.crypscape.persistance.entities.Candle
import app.lemley.crypscape.persistance.entities.Granularity
import app.lemley.crypscape.persistance.entities.Product
import app.lemley.crypscape.repository.converter.CandleConverter
import com.google.common.truth.Truth.assertThat
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Test
import app.lemley.crypscape.client.coinbase.model.Granularity as CbGranularity

@ExperimentalCoroutinesApi
class CoinBaseCandleRepositoryTest {

    private fun createRepository(
        apiClient: CoinBaseApi = mockk(relaxUnitFun = true),
        candleDao: CandleDao = mockk(relaxUnitFun = true),
        candleConverter: CandleConverter = mockk(relaxUnitFun = true),
        productDao: ProductDao = mockk(relaxUnitFun = true)
    ): CoinBaseCandleRepository = CoinBaseCandleRepository(
        apiClient,
        candleDao,
        candleConverter,
        productDao
    )

    @Test
    fun fetches_latest_candles_from_coinbase__persists_locally__returning_latest_candle_flow() {
        val configuration = MarketConfiguration(1, "BTC-USD", Granularity.Hour)
        val cbCandles: Array<Array<Double>> = arrayOf(
            arrayOf(
                1415398768.0, 0.32, 4.2, 0.35, 4.2, 12.3
            ), arrayOf(
                1415398767.0, 0.31, 4.1, 0.34, 4.1, 12.2
            )
        )
        val candle1 = mockk<Candle>()
        val candle2 = mockk<Candle>()
        val productServerId = "BTC-USD"
        val candleRequest: CandleRequest =
            CandleRequest(productId = productServerId, granularity = CbGranularity.Hour)

        val candleFlow = flowOf<List<Candle>>(mockk(), mockk())
        val productId: Long = 25
        val product: Product = mockk {
            every { id } returns 25
            every { serverId } returns productServerId
        }
        val productDao: ProductDao = mockk {
            every { byServerId(1, productServerId) } returns product
        }
        val candleDao: CandleDao = mockk(relaxUnitFun = true) {
            every {
                newestProductGranularityDistinctUntilChanged(
                    platformId = configuration.platformId,
                    productId = productId,
                    granularity = configuration.granularity.seconds
                )
            } returns candleFlow
        }

        val candleConverter = mockk<CandleConverter> {
            every {
                convert(
                    configuration.platformId,
                    productId,
                    configuration.granularity,
                    cbCandles[0]
                )
            } returns candle1
            every {
                convert(
                    configuration.platformId,
                    productId,
                    configuration.granularity,
                    cbCandles[1]
                )
            } returns candle2
        }

        val coinBaseApiClient: CoinBaseApi = mockk {
            every {
                runBlocking {
                    candlesFor(
                        candleRequest.productId,
                        candleRequest.asMap()
                    )
                }
            } returns cbCandles
        }

        val repository =
            createRepository(coinBaseApiClient, candleDao, candleConverter, productDao = productDao)

        runBlocking {
            assertThat(repository.candlesFor(configuration)).isEqualTo(candleFlow)
        }

        verify {
            candleDao.insertOrUpdate(candle1)
            candleDao.insertOrUpdate(candle2)
            candleDao.newestProductGranularityDistinctUntilChanged(
                platformId = configuration.platformId,
                productId = productId,
                granularity = configuration.granularity.seconds
            )
        }

        confirmVerified(candleDao)
    }

    @Test
    fun updates_period_with_ticker__updates_current_period__current__period() {
        val granularity = Granularity.Hour
        val timeStamp = "2020-03-09T18:06:45.386868Z"
        val period = granularity.periodForTickerTime(timeStamp)
        val candleForPeriod = Candle(
            product_id = 35,
            platform_id = 1,
            granularity = granularity,
            open = 5.0,
            close = 6.0,
            high = 6.5,
            low = 4.5,
            volume = 0.0,
            time = period
        )
        val tickers = listOf<Ticker>(
            Ticker(time = timeStamp, price = 6.1),
            Ticker(time = timeStamp, price = 7.0),
            Ticker(time = timeStamp, price = 4.0)
        )
        val candleDao: CandleDao = mockk(relaxed = true) {
            every {
                candleBy(
                    candleForPeriod.platform_id,
                    candleForPeriod.product_id,
                    granularity.seconds,
                    period.toEpochMilli()
                )
            } returns candleForPeriod
        }


        val marketConfiguration = MarketConfiguration(1, "BTC-USD", granularity)

        val productDao: ProductDao = mockk {
            every {
                byServerId(
                    marketConfiguration.platformId,
                    marketConfiguration.productRemoteId
                )
            } returns Product(
                platformId = marketConfiguration.platformId,
                id = candleForPeriod.product_id,
                quoteCurrency = 2,
                baseCurrency = 3
            )
        }

        val repository = createRepository(candleDao = candleDao, productDao = productDao)

        tickers.forEach {
            repository.updatePeriod(marketConfiguration, it)
        }

        val candlesToUpdate = mutableListOf<Candle>()
        verify { candleDao.insertOrUpdate(capture(candlesToUpdate)) }

        val expectedCandles = listOf<Candle>(
            candleForPeriod.copy(close = 6.1),
            candleForPeriod.copy(close = 7.0, high = 7.0),
            candleForPeriod.copy(close = 4.0, low = 4.0)

        )
        assertThat(candlesToUpdate).isEqualTo(expectedCandles)
    }


    @Test
    fun updates_period_with_ticker__updates_current_period__new__period() {
        val granularity = Granularity.Hour
        val timeStamp = "2020-03-09T18:06:45.386868Z"
        val period = granularity.periodForTickerTime(timeStamp)
        val candleForPreviousPeriod = Candle(
            product_id = 35,
            platform_id = 1,
            granularity = granularity,
            open = 5.0,
            close = 6.0,
            high = 6.5,
            low = 4.5,
            volume = 0.0,
            time = granularity.previousPeriod(period)
        )
        val tickers = listOf(
            Ticker(time = timeStamp, price = 6.0, volume = 10.0),
            Ticker(time = timeStamp, price = 7.0, volume = 10.0),
            Ticker(time = timeStamp, price = 4.0, volume = 10.0)
        )
        val candleDao: CandleDao = mockk(relaxed = true) {
            every {
                candleBy(
                    candleForPreviousPeriod.platform_id,
                    candleForPreviousPeriod.product_id,
                    granularity.seconds,
                    granularity.previousPeriod(period).toEpochMilli()
                )
            } returns candleForPreviousPeriod

            every {

                candleBy(
                    candleForPreviousPeriod.platform_id,
                    candleForPreviousPeriod.product_id,
                    granularity.seconds,
                    period.toEpochMilli()
                )
            } returns null
        }


        val marketConfiguration = MarketConfiguration(1, "BTC-USD", granularity)

        val productDao: ProductDao = mockk {
            every {
                byServerId(
                    marketConfiguration.platformId,
                    marketConfiguration.productRemoteId
                )
            } returns Product(
                platformId = marketConfiguration.platformId,
                id = candleForPreviousPeriod.product_id,
                quoteCurrency = 2,
                baseCurrency = 3
            )
        }

        val repository = createRepository(candleDao = candleDao, productDao = productDao)

        tickers.forEach {
            repository.updatePeriod(marketConfiguration, it)
        }

        val candlesToUpdate = mutableListOf<Candle>()
        verify { candleDao.insertOrUpdate(capture(candlesToUpdate)) }

        val expectedCandles = listOf<Candle>(
            candleForPreviousPeriod.copy(
                open = 6.0,
                close = 6.0,
                high = 6.0,
                low = 6.0,
                volume = 10.0,
                time = period
            ),
            candleForPreviousPeriod.copy(
                open = 6.0,
                close = 7.0,
                high = 7.0,
                low = 6.0,
                volume = 10.0,
                time = period
            ),
            candleForPreviousPeriod.copy(
                open = 6.0,
                close = 4.0,
                high = 6.0,
                low = 4.0,
                volume = 10.0,
                time = period
            )

        )
        assertThat(candlesToUpdate).isEqualTo(expectedCandles)
    }
}
