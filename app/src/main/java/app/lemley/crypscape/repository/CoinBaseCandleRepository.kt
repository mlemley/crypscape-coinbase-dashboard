package app.lemley.crypscape.repository

import app.lemley.crypscape.client.coinbase.CoinBaseApiClient
import app.lemley.crypscape.client.coinbase.model.CandleRequest
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
    private val coinBaseApiClient: CoinBaseApiClient,
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
                    val candles = coinBaseApiClient.candlesFor(
                        CandleRequest(
                            productId = product.serverId,
                            granularity = CBGranularity.fromSeconds(granularity.seconds)
                        )
                    )
                    candles?.forEach {
                        candleDao.insertOrUpdate(
                            candleConverter.convert(
                                platformId,
                                product.id,
                                granularity,
                                it
                            )
                        )
                    }
                    candleDao.newestProductGranularityDistinctUntilChanged(
                        platformId = platformId,
                        productId = product.id,
                        granularity = granularity.seconds
                    )
                }
            } ?: emptyFlow()
    }
}
