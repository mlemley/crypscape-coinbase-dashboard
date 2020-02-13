package app.lemley.crypscape.repository.converter

import app.lemley.crypscape.persistance.dao.CurrencyDao
import app.lemley.crypscape.persistance.entities.Platform
import app.lemley.crypscape.persistance.entities.Product
import app.lemley.crypscape.client.coinbase.model.Product as CBProduct

class ProductConverter {

    fun convert(platform: Platform, currencyDao: CurrencyDao, cbProduct: CBProduct): Product? {
        return currencyDao.currencyBy(platformId = platform.id, serverId = cbProduct.baseCurrency)
            ?.let { baseCurrency ->
                currencyDao.currencyBy(platformId = platform.id, serverId = cbProduct.quoteCurrency)
                    ?.let { quoteCurrency ->
                        return Product(
                            platformId = platform.id,
                            baseCurrency = baseCurrency.id,
                            quoteCurrency = quoteCurrency.id,
                            serverId = cbProduct.id,
                            baseMinSize = cbProduct.baseMinSize,
                            baseMaxSize = cbProduct.baseMaxSize,
                            quoteIncrement = cbProduct.quoteIncrement
                        )

                    } ?: return null
            }?: return null
    }
}
