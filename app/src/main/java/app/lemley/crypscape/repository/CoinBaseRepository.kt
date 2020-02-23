package app.lemley.crypscape.repository

import app.lemley.crypscape.model.MarketConfiguration
import app.lemley.crypscape.persistance.entities.Candle
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow


@ExperimentalCoroutinesApi
class CoinBaseRepository(
    val currencyRepository: CoinBaseCurrencyRepository,
    val productRepository: CoinBaseProductRepository,
    val candleRepository: CoinBaseCandleRepository
) {

    suspend fun syncProducts() {
        currencyRepository.sync()
        productRepository.sync()
    }

    suspend fun candlesForConfiguration(marketConfiguration: MarketConfiguration): Flow<List<Candle>> {
        return candleRepository.candlesFor(marketConfiguration)
    }
}