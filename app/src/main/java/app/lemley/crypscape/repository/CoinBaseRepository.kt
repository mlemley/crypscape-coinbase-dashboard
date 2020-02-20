package app.lemley.crypscape.repository

import app.lemley.crypscape.model.MarketConfiguration
import app.lemley.crypscape.persistance.entities.Candle


class CoinBaseRepository(
    val currencyRepository: CoinBaseCurrencyRepository,
    val productRepository: CoinBaseProductRepository,
    val candleRepository: CoinBaseCandleRepository
) {

    suspend fun syncProducts() {
        currencyRepository.sync()
        productRepository.sync()
    }

    suspend fun candlesForConfiguration(marketConfiguration: MarketConfiguration): List<Candle> {
        return candleRepository.candlesFor(marketConfiguration.remoteProductId, marketConfiguration.granularity)
    }
}