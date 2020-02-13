package app.lemley.crypscape.app

import android.app.Application
import app.lemley.crypscape.app.di.appModules
import com.jakewharton.threetenabp.AndroidThreeTen
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

@FlowPreview
@ExperimentalCoroutinesApi
open class CrypScapeApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
        loadKoin()
    }

    protected open fun loadKoin() {
        startKoin {
            androidLogger()
            androidContext(this@CrypScapeApplication)
            modules(appModules)
        }
    }
}