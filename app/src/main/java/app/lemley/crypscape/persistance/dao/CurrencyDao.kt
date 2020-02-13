package app.lemley.crypscape.persistance.dao

import androidx.annotation.WorkerThread
import androidx.room.Dao
import androidx.room.Query
import app.lemley.crypscape.persistance.entities.Currency
import app.lemley.crypscape.persistance.entities.Platform
import kotlinx.coroutines.flow.Flow


@Dao
abstract class CurrencyDao : BaseDao<Currency>() {

    @Query("SELECT * from currency ORDER BY id ASC")
    abstract fun all(): Flow<List<Currency>>

    @Query("SELECT * from currency where platformId = :platformId and  serverId = :serverId limit 1")
    abstract fun currencyBy(platformId: Long, serverId: String): Currency?

    @Query("DELETE FROM currency")
    abstract fun deleteAll()

    @WorkerThread
    fun insertOrUpdate(platform: Platform, currency: Currency) {
        val updated = currencyBy(platform.id, currency.serverId)?.let { existing ->
            update(
                existing.copy(
                    name = currency.name,
                    baseMinSize = currency.baseMinSize
                )
            )
            true
        } ?: false

        if (!updated)
            insert(currency.copy(platform_id = platform.id))
    }
}
