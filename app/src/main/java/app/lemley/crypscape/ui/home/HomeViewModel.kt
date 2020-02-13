package app.lemley.crypscape.ui.home

import app.lemley.crypscape.ui.base.*
import app.lemley.crypscape.usecase.UseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

@FlowPreview
@ExperimentalCoroutinesApi
class HomeViewModel : BaseViewModel<HomeViewModel.Events, HomeViewModel.HomeState>() {

    sealed class Events : Event {

    }

    data class HomeState(
        val foo: Boolean = false
    ) : State


    override val useCases: List<UseCase> = listOf()

    override fun makeInitState(): HomeState = HomeState()

    override fun Flow<Events>.eventTransform(): Flow<Action> {
        return emptyFlow()
    }

    override fun HomeState.plus(result: Result): HomeState {
        return when (result) {

            else -> this
        }
    }

}