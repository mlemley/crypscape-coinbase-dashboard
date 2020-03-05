package app.lemley.crypscape.repository

import app.lemley.crypscape.client.coinbase.CoinBaseApi
import app.lemley.crypscape.persistance.dao.CurrencyDao
import app.lemley.crypscape.persistance.dao.PlatformDao
import app.lemley.crypscape.repository.converter.CurrencyConverter

class CoinBaseCurrencyRepository(
    private val coinBaseApi: CoinBaseApi,
    private val currencyConverter: CurrencyConverter,
    private val currencyDao: CurrencyDao,
    private val platformDao: PlatformDao

) {
    suspend fun sync() {
        platformDao.coinbasePro?.let {platform ->
            coinBaseApi.currencies().forEach { coinBaseCurrency ->
                currencyDao.insertOrUpdate(platform, currencyConverter.convert(coinBaseCurrency))
            }
        }
    }
}
