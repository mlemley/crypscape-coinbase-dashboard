package app.lemley.crypscape.app

import android.app.Application
import androidx.annotation.VisibleForTesting
import app.lemley.crypscape.app.di.AppModule.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

open class CrypScapeApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        loadKoin()
    }

    protected open fun loadKoin() {
        startKoin {
            androidLogger()
            androidContext(this@CrypScapeApplication)
            modules(appModule)
        }
    }
}