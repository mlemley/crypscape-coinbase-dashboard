package app.lemley.crypscape.app

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers


open class CoroutineContextProvider {
    open val Main: CoroutineDispatcher = Dispatchers.Main
    open val IO: CoroutineDispatcher = Dispatchers.IO
    open val Default: CoroutineDispatcher = Dispatchers.Default
}

