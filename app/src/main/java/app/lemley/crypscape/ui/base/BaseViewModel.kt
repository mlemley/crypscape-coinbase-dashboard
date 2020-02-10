package app.lemley.crypscape.ui.base

import com.crashlytics.android.Crashlytics
import android.util.Log
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import app.lemley.crypscape.usecase.UseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*

interface State
interface Event
interface Action
interface Result

@FlowPreview
@ExperimentalCoroutinesApi
abstract class BaseViewModel<E:Event, S: State> : ViewModel() {

    private var events: Channel<E> = Channel(Channel.UNLIMITED)
        get() {
            if (field.isClosedForSend || field.isClosedForReceive)
                field = Channel(Channel.UNLIMITED)
            return field
        }


    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    abstract val useCases: List<UseCase>

    val state = liveData<S> {
        emit(latestValue ?: makeInitState())
        events.consumeAsFlow()
            .eventTransform()
            .actionTransform()
            .map {
                (latestValue ?: makeInitState()) + it
            }
            .distinctUntilChanged()
            .catch {
                Crashlytics.logException(it)
                Log.e("BaseModelView", it.localizedMessage)
                it.printStackTrace()
            }
            .onEach {
                emit(it)
            }
            .collect()
    }

    fun dispatchEvent(event: E) {
        events.offer(event)
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    abstract fun makeInitState(): S

    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    abstract fun Flow<E>.eventTransform(): Flow<Action>

    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    abstract operator fun S.plus(result: Result): S

    private fun Flow<Action>.actionTransform(): Flow<Result> = flatMapMerge { action ->
        useCases.filter { it.canProcess(action) }.asFlow().flatMapMerge { it.handleAction(action) }
    }

}
