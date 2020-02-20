package app.lemley.crypscape.repository

import android.content.SharedPreferences
import androidx.annotation.VisibleForTesting
import app.lemley.crypscape.model.MarketConfiguration
import app.lemley.crypscape.persistance.dao.PlatformDao
import app.lemley.crypscape.persistance.dao.ProductDao
import app.lemley.crypscape.persistance.entities.Granularity

class DefaultMarketDataRepository constructor(
    val sharedPreferences: SharedPreferences,
    val platformDao: PlatformDao,
    val productDao: ProductDao
) {

    companion object {
        @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
        const val preferenceKey = "default_market_configuration"

        @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
        const val defaultProductId = "BTC-USD"
    }

    suspend fun loadDefault(): MarketConfiguration {
        return if (sharedPreferences.contains(preferenceKey)) {
            return MarketConfiguration.fromJson(
                sharedPreferences.getString(preferenceKey, null)
                    ?: throw IllegalStateException("Platform can not be null")
            )
        } else {
            createDefault().also {
                sharedPreferences.edit().putString(it.toJson(), null).apply()
            }
        }
    }


    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun createDefault(): MarketConfiguration {
        val platform =
            platformDao.coinbasePro ?: throw IllegalStateException("Platform can not be null")
        val product = productDao.byServerId(platform.id, defaultProductId)
            ?: throw IllegalStateException("Product with server id $defaultProductId must be stored")
        return MarketConfiguration(product, Granularity.Hour)
    }

}
