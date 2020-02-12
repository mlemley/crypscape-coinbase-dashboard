package app.lemley.crypscape.app

import androidx.test.core.app.ApplicationProvider
import org.koin.core.context.loadKoinModules
import org.koin.core.module.Module


object Helpers {
    fun loadModules(vararg modules: Module) {
        ApplicationProvider.getApplicationContext<TestCrypScapeApplication>().also {
            loadKoinModules(
                modules.toList()
            )
        }
    }
}

