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

    single { CrypScapeDatabaseConnectionCallback(get()) }

    // Expose DAO's

    single { get<CrypScapeDb>().platformDao }
    single { get<CrypScapeDb>().candleDao }
    single { get<CrypScapeDb>().productDao }
}
