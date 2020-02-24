package app.lemley.crypscape.ui.market

import app.lemley.crypscape.extensions.exhaustive
import app.lemley.crypscape.ui.base.Action
import app.lemley.crypscape.ui.base.BaseViewModel
import app.lemley.crypscape.ui.base.Result
import app.lemley.crypscape.usecase.UseCase
import app.lemley.crypscape.usecase.MarketDataUseCase
import app.lemley.crypscape.usecase.MarketDataUseCase.MarketActions
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow

@FlowPreview
@ExperimentalCoroutinesApi
class MarketViewModel(
    marketDataUseCase: MarketDataUseCase
) : BaseViewModel<MarketEvents, MarketState>() {


    override val useCases: List<UseCase> = listOf(marketDataUseCase)

    override fun makeInitState(): MarketState = MarketState()

    override fun Flow<MarketEvents>.eventTransform(): Flow<Action> = flow {
        collect {
            when (it) {
                MarketEvents.Init -> emit(MarketActions.FetchMarketDataForDefaultConfiguration)
                is MarketEvents.GranularitySelected -> emit(MarketActions.OnGranularityChanged(it.granularity))
            }.exhaustive
        }
    }

    override fun MarketState.plus(result: Result): MarketState {
        return when (result) {
            is MarketDataUseCase.MarketResults.MarketConfigurationResult -> copy(
                marketConfiguration = result.marketConfiguration
            )
            is MarketDataUseCase.MarketResults.CandlesForConfigurationResult -> copy(
                candles = result.candles
            )
            is MarketDataUseCase.MarketResults.TickerResult -> copy(
                ticker = result.ticker
            )
            else -> this
        }
    }

}