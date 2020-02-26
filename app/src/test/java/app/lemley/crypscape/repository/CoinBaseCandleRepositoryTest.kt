package app.lemley.crypscape.repository

import app.lemley.crypscape.client.coinbase.CoinBaseApiClient
import app.lemley.crypscape.client.coinbase.model.CandleRequest
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
        apiClient: CoinBaseApiClient = mockk(relaxUnitFun = true),
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
        val productId:Long = 25
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

        val coinBaseApiClient: CoinBaseApiClient = mockk {
            every { runBlocking { candlesFor(candleRequest) } } returns cbCandles
        }

        val repository = createRepository(coinBaseApiClient, candleDao, candleConverter, productDao = productDao)

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
}