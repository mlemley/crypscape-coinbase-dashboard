package app.lemley.crypscape.app

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.InternalCoroutinesApi
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.robolectric.TestLifecycleApplication
import java.lang.reflect.Method

@FlowPreview
@ExperimentalCoroutinesApi
class TestCrypScapeApplication : TestLifecycleApplication, CrypScapeApplication() {

    override fun loadKoin() { }
    override fun startSocketIO() { }

    override fun beforeTest(method: Method?) {
        startKoin {
            androidLogger()
            androidContext(this@TestCrypScapeApplication)
        }
    }

    override fun prepareTest(test: Any?) {
    }

    override fun afterTest(method: Method?) {
        stopKoin()
    }

}