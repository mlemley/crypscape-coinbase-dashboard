package app.lemley.crypscape.persistance

import androidx.annotation.WorkerThread
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import app.lemley.crypscape.extensions.toInstant
import app.lemley.crypscape.persistance.entities.Platform
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.threeten.bp.Instant

class CrypScapeDatabaseConnectionCallback : RoomDatabase.Callback() {

    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        GlobalScope.launch(Dispatchers.Main) {
            withContext(Dispatchers.IO) {
                populateInitialPlatforms()
            }
        }
    }

    @WorkerThread
    private suspend fun populateInitialPlatforms() {
        CrypScapeDb.Instance?.platformDao?.insert(
            Platform(
                name = "Coinbase Pro",
                startDate = "2017-12-01T00:00Z".toInstant() ?: Instant.now()
            )
        )
    }
}
