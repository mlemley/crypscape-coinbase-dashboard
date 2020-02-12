package app.lemley.crypscape.persistance.dao

import androidx.room.Dao
import androidx.room.Query
import app.lemley.crypscape.persistance.entities.Platform
import kotlinx.coroutines.flow.Flow

@Dao
abstract class PlatformDao : BaseDao<Platform>() {
    val coinbasePro: Platform?
        get() {
            val platforms = byName("Coinbase Pro")
            return when {
                platforms.size > 1 ->
                    // clean up
                    platforms[0]
                platforms.isNotEmpty() ->
                    platforms[0]
                else ->
                    null
            }
        }

    @Query("SELECT * from platform where name = :name")
    abstract fun byName(name: String): List<Platform>

    @Query("SELECT * from platform ORDER BY id ASC")
    abstract fun all(): Flow<List<Platform>>

    @Query("DELETE FROM platform")
    abstract fun deleteAll()


}