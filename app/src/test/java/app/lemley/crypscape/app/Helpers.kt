package app.lemley.crypscape.app

import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.InternalCoroutinesApi
import org.koin.core.context.loadKoinModules
import org.koin.core.module.Module


@FlowPreview
@ExperimentalCoroutinesApi
object Helpers {
    fun loadModules(vararg modules: Module) {
        ApplicationProvider.getApplicationContext<TestCrypScapeApplication>().also {
            loadKoinModules(
                modules.toList()
            )
        }
    }
}

