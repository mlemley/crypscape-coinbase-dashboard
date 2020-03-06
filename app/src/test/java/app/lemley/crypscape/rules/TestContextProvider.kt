package app.lemley.crypscape.rules

import app.lemley.crypscape.app.CoroutineContextProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class TestContextProvider: CoroutineContextProvider() {
    override val Main: CoroutineDispatcher = Dispatchers.Unconfined
    override val IO: CoroutineDispatcher = Dispatchers.Unconfined
    override val Default: CoroutineDispatcher = Dispatchers.Unconfined
}