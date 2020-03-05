package app.lemley.crypscape.repository

import app.lemley.crypscape.client.coinbase.CoinBaseApi
import app.lemley.crypscape.persistance.dao.CurrencyDao
import app.lemley.crypscape.persistance.dao.PlatformDao
import app.lemley.crypscape.persistance.dao.ProductDao
import app.lemley.crypscape.repository.converter.ProductConverter

class CoinBaseProductRepository constructor(
    private val coinBaseApi: CoinBaseApi,
    private val productConverter: ProductConverter,
    private val productDao: ProductDao,
    private val currencyDao: CurrencyDao,
    private val platformDao: PlatformDao
) {
    suspend fun sync() {
        platformDao.coinbasePro?.let { coinBase ->
            coinBaseApi.products().forEach { product ->
                productConverter.convert(coinBase, currencyDao, product)?.let {
                    productDao.insertOrUpdate(it)
                }
            }
        }
    }
}
