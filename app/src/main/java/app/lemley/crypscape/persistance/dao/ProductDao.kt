package app.lemley.crypscape.persistance.dao

import androidx.annotation.WorkerThread
import androidx.room.Dao
import androidx.room.Query
import app.lemley.crypscape.persistance.entities.Currency
import app.lemley.crypscape.persistance.entities.Platform
import app.lemley.crypscape.persistance.entities.Product
import kotlinx.coroutines.flow.Flow
import app.lemley.crypscape.client.coinbase.model.Product as CoinBaseProduct

@Dao
abstract class ProductDao : BaseDao<Product>() {

    @Query("SELECT * from product ORDER BY id ASC")
    abstract fun all(): Flow<List<Product>>

    @Query("SELECT * from product where id = :productId")
    abstract fun by(productId: Long): Product?

    @Query("SELECT * from product where platformId = :platformId and base_currency_id = :baseId and quote_currency_id = :quoteId limit 1")
    abstract fun by(platformId: Long, baseId: Long, quoteId: Long): Product?

    @Query("SELECT * from product where platformId = :platformId and serverId = :serverId limit 1")
    abstract fun byServerId(platformId: Long, serverId: String): Product?

    @Query("DELETE FROM product")
    abstract fun deleteAll()

    @WorkerThread
    fun insertOrUpdate(product: Product) {
        var updated = by(product.platformId, product.baseCurrency, product.quoteCurrency)?.let {
            update(
                it.copy(
                    baseMaxSize = product.baseMaxSize,
                    baseMinSize = product.baseMinSize,
                    quoteIncrement = product.quoteIncrement
                )
            )
            true
        } ?: false

        if (!updated) insert(product.copy(platformId = product.platformId))
    }
}