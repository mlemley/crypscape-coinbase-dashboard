package app.lemley.crypscape.ui.splash

import app.lemley.crypscape.extensions.exhaustive
import app.lemley.crypscape.ui.base.Action
import app.lemley.crypscape.ui.base.BaseViewModel
import app.lemley.crypscape.ui.base.Event
import app.lemley.crypscape.ui.base.Result
import app.lemley.crypscape.ui.base.State
import app.lemley.crypscape.usecase.DelayedCallback
import app.lemley.crypscape.usecase.SyncProductUseCase
import app.lemley.crypscape.usecase.UseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow

@FlowPreview
@ExperimentalCoroutinesApi
class SplashViewModel(
    syncProductUseCase: SyncProductUseCase
) : BaseViewModel<SplashViewModel.Events, SplashViewModel.SplashState>() {

    sealed class RequiredActions {
        object ProgressForward : RequiredActions()
    }

    sealed class Events : Event {
        object Loaded : Events()
    }

    data class SplashState(
        val requiredActions: RequiredActions? = null
    ) : State

    override val useCases: List<UseCase> = listOf(syncProductUseCase)

    override fun makeInitState(): SplashState = SplashState()

    override fun Flow<Events>.eventTransform(): Flow<Action> = flow {
        collect {
            when (it) {
                is Events.Loaded -> emit(SyncProductUseCase.SyncProductData)
            }.exhaustive
        }
    }

    override fun SplashState.plus(result: Result): SplashState {
        return when(result) {
            is SyncProductUseCase.ProductSyncComplete -> copy(
                requiredActions = RequiredActions.ProgressForward
            )
            else -> this
        }
    }
}