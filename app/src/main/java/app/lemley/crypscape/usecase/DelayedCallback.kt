package app.lemley.crypscape.usecase

import app.lemley.crypscape.ui.base.Action
import app.lemley.crypscape.ui.base.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOn

@ExperimentalCoroutinesApi
class DelayedCallback : UseCase {

    data class DelayFor(val millis: Long) : Action
    object DelayCompletedResult: Result


    override fun canProcess(action: Action): Boolean {
        return action is DelayFor
    }

    override fun handleAction(action: Action): Flow<Result> {
        return when(action) {
            is DelayFor -> handleDelay(action.millis)
            else -> emptyFlow()
        }
    }

    private fun handleDelay(millis: Long) = channelFlow<Result> {
        delay(millis)
        this.send(DelayCompletedResult)
    }.flowOn(Dispatchers.IO)

}
