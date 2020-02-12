package app.lemley.crypscape.persistance.dao

import androidx.annotation.WorkerThread
import androidx.room.Dao
import androidx.room.Query
import app.lemley.crypscape.persistance.entities.Currency
import app.lemley.crypscape.persistance.entities.Platform
import kotlinx.coroutines.flow.Flow
import app.lemley.crypscape.client.coinbase.model.Currency as CoinBaseCurrency


@Dao
abstract class CurrencyDao : BaseDao<Currency>() {

    @Query("SELECT * from currency ORDER BY id ASC")
    abstract fun all(): Flow<List<Currency>>

    @Query("SELECT * from currency where platformId = :platformId and  serverId = :serverId limit 1")
    abstract fun currencyBy(platformId: Long, serverId: String): Currency?

    @Query("DELETE FROM currency")
    abstract fun deleteAll()

    @WorkerThread
    fun insertOrUpdate(currency: Currency) {
        var updated = false
        currency.platform_id?.let { platform_id ->
            currencyBy(platform_id, currency.serverId)?.let { existing ->
                update(
                    existing.copy(
                        name = currency.name,
                        baseMinSize = currency.baseMinSize
                    )
                )
                updated = true
            }
        }

        if (!updated)
            insert(currency)
    }

    fun newFrom(platform: Platform, currency: CoinBaseCurrency): Currency {
        return Currency(
            platform_id = platform.id,
            serverId = currency.id,
            name = currency.name,
            baseMinSize = currency.minSize
        )
    }
}
