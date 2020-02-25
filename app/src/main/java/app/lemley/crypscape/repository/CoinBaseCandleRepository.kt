package app.lemley.crypscape.repository

import app.lemley.crypscape.client.coinbase.CoinBaseApiClient
import app.lemley.crypscape.client.coinbase.model.CandleRequest
import app.lemley.crypscape.client.coinbase.model.Granularity
import app.lemley.crypscape.model.MarketConfiguration
import app.lemley.crypscape.persistance.dao.CandleDao
import app.lemley.crypscape.persistance.entities.Candle
import app.lemley.crypscape.repository.converter.CandleConverter
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
class CoinBaseCandleRepository constructor(
    private val coinBaseApiClient: CoinBaseApiClient,
    private val candleDao: CandleDao,
    private val candleConverter: CandleConverter
) {

    suspend fun candlesFor(marketConfiguration: MarketConfiguration): Flow<List<Candle>> {
        fetchLatestCandles(marketConfiguration)
        return with(marketConfiguration) {
            candleDao.newestProductGranularityDistinctUntilChanged(
                platformId = product.platformId,
                productId = product.id,
                granularity = granularity.seconds
            )
        }
    }

    private fun fetchLatestCandles(marketConfiguration: MarketConfiguration) {
        with(marketConfiguration) {
            GlobalScope.launch {
                async {
                    val candles = coinBaseApiClient.candlesFor(
                        CandleRequest(
                            productId = product.serverId,
                            granularity = Granularity.fromSeconds(granularity.seconds)
                        )
                    )
                    candles?.forEach {
                        candleDao.insertOrUpdate(
                            candleConverter.convert(
                                product.platformId,
                                product.id,
                                granularity,
                                it
                            )
                        )
                    }
                }
            }
        }
    }
}
