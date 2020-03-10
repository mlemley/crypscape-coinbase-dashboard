package app.lemley.crypscape.persistance

import androidx.annotation.WorkerThread
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import app.lemley.crypscape.extensions.toInstant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.threeten.bp.Instant

class CrypScapeDatabaseConnectionCallback : RoomDatabase.Callback() {

    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        populateInitialPlatforms(db)
    }

    private fun populateInitialPlatforms(db: SupportSQLiteDatabase) {
        val startDate = "2017-12-01T00:00Z".toInstant() ?: Instant.now()
        db.execSQL("""
            insert into `platform`(name, startDate)  
            values ("Coinbase Pro", ${startDate.toEpochMilli()})
        """)
    }
}
