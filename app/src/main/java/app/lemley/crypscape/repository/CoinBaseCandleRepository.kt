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
import app.lemley.crypscape.repository.converter.CandleConverter
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import app.lemley.crypscape.client.coinbase.model.Granularity as CBGranularity

@ExperimentalCoroutinesApi
class CoinBaseCandleRepository constructor(
    private val coinBaseApi: CoinBaseApi,
    private val candleDao: CandleDao,
    private val candleConverter: CandleConverter,
    private val productDao: ProductDao
) {

    suspend fun candlesFor(marketConfiguration: MarketConfiguration): Flow<List<Candle>> {
        return productDao.byServerId(
                marketConfiguration.platformId,
                marketConfiguration.productRemoteId
            )
            ?.let { product ->
                with(marketConfiguration) {
                    CandleRequest(
                        productId = product.serverId,
                        granularity = CBGranularity.fromSeconds(granularity.seconds)
                    ).also {
                        coinBaseApi.candlesFor(it.productId, it.asMap()).forEach { data ->
                            candleDao.insertOrUpdate(
                                candleConverter.convert(
                                    platformId,
                                    product.id,
                                    granularity,
                                    data
                                )
                            )
                        }
                    }
                    candleDao.newestProductGranularityDistinctUntilChanged(
                        platformId = platformId,
                        productId = product.id,
                        granularity = granularity.seconds
                    )
                }
            } ?: emptyFlow()
    }

    fun updatePeriod(marketConfiguration: MarketConfiguration, ticker: Ticker) {
        productDao.byServerId(marketConfiguration.platformId, marketConfiguration.productRemoteId)
            ?.let { product ->
                val granularity = marketConfiguration.granularity
                val period = granularity.periodForTickerTime(ticker.time)
                val platformId = product.platformId
                val productId = product.id
                val price = ticker.price
                candleDao.candleBy(
                    platformId = platformId,
                    productId = productId,
                    granularity = granularity.seconds,
                    time = period.toEpochMilli()
                )?.let { currentCandle ->
                    // update Current
                    candleDao.insertOrUpdate(
                        currentCandle.copy(
                            close = price,
                            high = if (currentCandle.high > price) currentCandle.high else price,
                            low = if (currentCandle.low < price) currentCandle.low else price
                        )
                    )
                } ?: candleDao.candleBy(
                    platformId = platformId,
                    productId = productId,
                    granularity = granularity.seconds,
                    time = granularity.previousPeriod(period).toEpochMilli()
                )?.let { previousCandle ->

                    // Create new candle based on previous data
                    val open = previousCandle.close

                    val candle = Candle(
                        platform_id = platformId,
                        product_id = productId,
                        granularity = granularity,
                        time = period,
                        open = open,
                        close = price,
                        high = if (open > price) open else price,
                        low = if (open < price) open else price,
                        volume = ticker.volume
                    )
                    candleDao.insertOrUpdate(candle)
                }


            }
    }
}
