package app.lemley.crypscape.persistance

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import app.lemley.crypscape.persistance.dao.CandleDao
import app.lemley.crypscape.persistance.dao.CurrencyDao
import app.lemley.crypscape.persistance.dao.PlatformDao
import app.lemley.crypscape.persistance.dao.ProductDao
import app.lemley.crypscape.persistance.entities.Candle
import app.lemley.crypscape.persistance.entities.Currency
import app.lemley.crypscape.persistance.entities.Platform
import app.lemley.crypscape.persistance.entities.Product
import com.crypscape.mobile.db.converter.DateConverter
import kotlinx.coroutines.ExperimentalCoroutinesApi

@Database(
    entities = [
        Platform::class,
        Candle::class,
        Currency::class,
        Product::class
    ],
    version = 1
)
@TypeConverters(DateConverter::class)
@ExperimentalCoroutinesApi
abstract class CrypScapeDb : RoomDatabase() {
    abstract val candleDao: CandleDao
    abstract val currencyDao: CurrencyDao
    abstract val productDao: ProductDao
    abstract val platformDao: PlatformDao
}