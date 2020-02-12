package app.lemley.crypscape.persistance

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import app.lemley.crypscape.persistance.dao.CurrencyDao
import app.lemley.crypscape.persistance.entities.Candle
import app.lemley.crypscape.persistance.entities.Currency
import app.lemley.crypscape.persistance.entities.Platform
import app.lemley.crypscape.persistance.entities.Product
import com.crypscape.mobile.db.converter.DateConverter
import app.lemley.crypscape.persistance.dao.CandleDao
import app.lemley.crypscape.persistance.dao.PlatformDao
import app.lemley.crypscape.persistance.dao.ProductDao

@Database(
    entities = [
        Platform::class,
        Candle::class,
        Currency::class,
        Product::class
    ],
    version = 0
)
@TypeConverters(DateConverter::class)
abstract class CrypScapeDb : RoomDatabase() {
    abstract val candleDao: CandleDao
    abstract val currencyDao: CurrencyDao
    abstract val productDao: ProductDao
    abstract val platformDao: PlatformDao

    companion object {
        @Volatile
        internal var Instance: CrypScapeDb? = null
        private const val DB_NAME = "crypscape_db"

        fun getDatabase(context: Context, crypscapeDatabaseConnectionCallback: CrypScapeDatabaseConnectionCallback): CrypScapeDb {
            Instance?.let { return it }

            synchronized(this) {
                val instance =
                    Room.databaseBuilder(context.applicationContext, CrypScapeDb::class.java, DB_NAME)
                        .allowMainThreadQueries()
                        .addMigrations( )
                        .addCallback(crypscapeDatabaseConnectionCallback)
                        .build()
                Instance = instance
                return instance
            }
        }
    }
}