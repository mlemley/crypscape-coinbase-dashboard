package app.lemley.crypscape.persistance

import androidx.room.Room
import org.koin.android.ext.koin.androidApplication
import org.koin.core.qualifier.named
import org.koin.dsl.module

val persistenceModule = module {

    single(named("dbName")) { "crypscape_db" }

    single {
        Room.databaseBuilder(
            androidApplication(),
            CrypScapeDb::class.java,
            get(named("dbName"))
        )
            .allowMainThreadQueries()
            .addMigrations()
            .addCallback(get<CrypScapeDatabaseConnectionCallback>())
            .build()
    }

    single { CrypScapeDatabaseConnectionCallback() }

    // Expose DAO's

    factory { get<CrypScapeDb>().platformDao }
    factory { get<CrypScapeDb>().currencyDao }
    factory { get<CrypScapeDb>().productDao }
    factory { get<CrypScapeDb>().candleDao }
}
