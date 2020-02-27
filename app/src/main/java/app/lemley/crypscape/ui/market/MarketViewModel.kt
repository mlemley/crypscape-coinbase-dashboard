package app.lemley.crypscape.ui.market

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import app.lemley.crypscape.extensions.exhaustive
import app.lemley.crypscape.model.MarketConfiguration
import app.lemley.crypscape.persistance.entities.Candle
import app.lemley.crypscape.repository.CoinBaseRepository
import app.lemley.crypscape.ui.base.Action
import app.lemley.crypscape.ui.base.BaseViewModel
import app.lemley.crypscape.ui.base.Result
import app.lemley.crypscape.usecase.MarketDataUseCase
import app.lemley.crypscape.usecase.MarketDataUseCase.MarketActions
import app.lemley.crypscape.usecase.UseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.*

@FlowPreview
@ExperimentalCoroutinesApi
class MarketViewModel(
    marketDataUseCase: MarketDataUseCase,
    coinBaseRepository: CoinBaseRepository
) : BaseViewModel<MarketEvents, MarketState>() {


    class CandleFilter(val marketConfiguration: MarketConfiguration)

    private val candleChannel = ConflatedBroadcastChannel<CandleFilter>()

    val candles: LiveData<List<Candle>> = candleChannel.asFlow()
        .flatMapLatest { filter ->
            coinBaseRepository.candlesForConfiguration(filter.marketConfiguration)
        }
        .flowOn(Dispatchers.IO)
        .asLiveData()

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
            ).also {
                candleChannel.offer(CandleFilter(result.marketConfiguration))
            }

            is MarketDataUseCase.MarketResults.TickerResult -> copy(
                ticker = result.ticker
            )
            else -> this
        }
    }

}