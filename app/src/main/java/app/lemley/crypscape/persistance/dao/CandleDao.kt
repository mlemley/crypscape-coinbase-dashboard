package app.lemley.crypscape.persistance.dao

import androidx.annotation.WorkerThread
import androidx.room.Dao
import androidx.room.Query
import app.lemley.crypscape.extensions.toInstant
import app.lemley.crypscape.persistance.entities.Candle
import app.lemley.crypscape.persistance.entities.Granularity
import app.lemley.crypscape.persistance.entities.Platform
import app.lemley.crypscape.persistance.entities.Product
import kotlinx.coroutines.flow.Flow

@Dao
abstract class CandleDao : BaseDao<Candle>() {

    @Query(
        """
        SELECT * from candle 
        where platformId = :platformId 
            and product_id = :productId 
            and granularity = :granularity 
            and time = :time 
        limit 1
    """
    )
    abstract fun candleBy(platformId: Long, productId: Long, granularity: Long, time: Long): Candle?

    @Query("SELECT * from candle ORDER BY id ASC")
    abstract fun all(): Flow<List<Candle>>

    @Query("DELETE FROM candle")
    abstract fun deleteAll()


    @WorkerThread
    fun insertOrUpdate(candle: Candle) {
        val updated = candleBy(
            candle.platform_id,
            candle.product_id,
            candle.granularity.seconds,
            candle.time.toEpochMilli()
        )?.let { current ->
            update(
                current.copy(
                    low = candle.low,
                    high = candle.high,
                    open = candle.open,
                    close = candle.close,
                    volume = candle.volume
                )
            )
            true
        } ?: false

        if (!updated)
            insert(candle)
    }

    fun newFrom(
        platform: Platform,
        product: Product,
        granularity: Granularity,
        candleData: Array<Double>
    ): Candle {
        return Candle(
            platform_id = platform.id,
            product_id = product.id,
            granularity = granularity,
            time = candleData[0].toInstant(),
            low = candleData[1],
            high = candleData[2],
            open = candleData[3],
            close = candleData[4],
            volume = candleData[5]
        )
    }


}