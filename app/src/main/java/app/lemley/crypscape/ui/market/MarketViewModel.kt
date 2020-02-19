package app.lemley.crypscape.ui.market

import app.lemley.crypscape.extensions.exhaustive
import app.lemley.crypscape.ui.base.*
import app.lemley.crypscape.usecase.UseCase
import app.lemley.crypscape.usecasei.MarketDataUseCase
import app.lemley.crypscape.usecasei.MarketDataUseCase.MarketActions
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow

@FlowPreview
@ExperimentalCoroutinesApi
class MarketViewModel(
    marketDataUseCase: MarketDataUseCase
) : BaseViewModel<MarketViewModel.Events, MarketViewModel.HomeState>() {

    sealed class Events : Event {
        object Init : Events()
    }

    data class HomeState(
        val foo: Boolean = false
    ) : State


    override val useCases: List<UseCase> = listOf(marketDataUseCase)

    override fun makeInitState(): HomeState = HomeState()

    override fun Flow<Events>.eventTransform(): Flow<Action> = flow {
        collect {
            when (it) {
                Events.Init -> emit(MarketActions.FetchMarketDataForDefaultConfiguration)
            }.exhaustive
        }
    }

    override fun HomeState.plus(result: Result): HomeState {
        return when (result) {

            else -> this
        }
    }

}