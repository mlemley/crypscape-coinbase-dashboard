package app.lemley.crypscape.repository

import app.lemley.crypscape.client.coinbase.CoinBaseApiClient
import app.lemley.crypscape.persistance.dao.CurrencyDao
import app.lemley.crypscape.persistance.dao.PlatformDao
import app.lemley.crypscape.persistance.dao.ProductDao
import app.lemley.crypscape.repository.converter.ProductConverter

class CoinBaseProductRepository constructor(
    private val coinBaseApiClient: CoinBaseApiClient,
    private val productConverter: ProductConverter,
    private val productDao: ProductDao,
    private val currencyDao: CurrencyDao,
    private val platformDao: PlatformDao
) {
    suspend fun sync() {
        platformDao.coinbasePro?.let { coinBase ->
            coinBaseApiClient.products()?.forEach { product ->
                productConverter.convert(coinBase, currencyDao, product)?.let {
                    productDao.insertOrUpdate(it)
                }
            }
        }
    }
}
