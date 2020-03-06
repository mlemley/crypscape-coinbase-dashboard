package app.lemley.crypscape.ui.market

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import app.lemley.crypscape.app.CoroutineContextProvider
import app.lemley.crypscape.client.coinbase.CoinBaseWSService
import app.lemley.crypscape.client.coinbase.model.Subscribe
import app.lemley.crypscape.extensions.exhaustive
import app.lemley.crypscape.model.MarketConfiguration
import app.lemley.crypscape.persistance.entities.Candle
import app.lemley.crypscape.repository.CoinBaseRealTimeRepository
import app.lemley.crypscape.repository.CoinBaseRepository
import app.lemley.crypscape.ui.base.Action
import app.lemley.crypscape.ui.base.BaseViewModel
import app.lemley.crypscape.ui.base.Result
import app.lemley.crypscape.usecase.MarketDataUseCase
import app.lemley.crypscape.usecase.MarketDataUseCase.MarketActions
import app.lemley.crypscape.usecase.UseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@FlowPreview
@ExperimentalCoroutinesApi
class MarketViewModel(
    marketDataUseCase: MarketDataUseCase,
    coinBaseRepository: CoinBaseRepository,
    val coinBaseRealTimeRepository: CoinBaseRealTimeRepository,
    val contextProvider: CoroutineContextProvider
) : BaseViewModel<MarketEvents, MarketState>() {

    init {
        viewModelScope.launch {
            coinBaseRealTimeRepository.connectionStateFlow.collect {
                dispatchEvent(
                    MarketEvents.RealtimeConnectionChangedEvent(
                        it is CoinBaseRealTimeRepository.ConnectionState.Connected
                    )
                )
            }
        }

        viewModelScope.launch {
            coinBaseRealTimeRepository.tickerFlow
                .filter {
                    it.productId == productId
                }
                .onEach {
                    dispatchEvent(MarketEvents.TickerChangedEvent(it))
                }
                .conflate()
                .collect()

        }
    }

    private var productId: String? = null
        set(value) {
            field = value
            viewModelScope.launch {
                value?.let {
                    coinBaseRealTimeRepository.subscribe(
                        listOf(it),
                        listOf(Subscribe.Channel.Ticker)
                    )
                }
            }
        }

    data class CandleFilter(val marketConfiguration: MarketConfiguration)

    private val candleChannel = ConflatedBroadcastChannel<CandleFilter>()
    val candles: LiveData<List<Candle>> = candleChannel.asFlow()
        .flatMapLatest { filter ->
            coinBaseRepository.candlesForConfiguration(filter.marketConfiguration)
        }
        .flowOn(contextProvider.IO)
        .asLiveData()

    override val useCases: List<UseCase> = listOf(marketDataUseCase)

    override fun makeInitState(): MarketState = MarketState()

    override fun Flow<MarketEvents>.eventTransform(): Flow<Action> = flow {
        collect {
            when (it) {
                is MarketEvents.Init -> emit(MarketActions.FetchMarketDataForDefaultConfiguration)
                is MarketEvents.GranularitySelected -> emit(
                    MarketActions.OnGranularityChanged(
                        it.granularity
                    )
                )
                is MarketEvents.TickerChangedEvent -> emit(MarketActions.OnTickerTick(it.ticker))
                is MarketEvents.RealtimeConnectionChangedEvent -> emit(
                    MarketActions.OnConnectionChanged(
                        it.hasConnection
                    )
                )
            }.exhaustive
        }
    }

    override fun MarketState.plus(result: Result): MarketState {
        return when (result) {
            is MarketDataUseCase.MarketResults.MarketConfigurationResult -> copy(
                marketConfiguration = result.marketConfiguration
            ).also {
                candleChannel.offer(CandleFilter(result.marketConfiguration))
                productId = result.marketConfiguration.productRemoteId
            }

            is MarketDataUseCase.MarketResults.TickerResult -> copy(
                ticker = result.ticker
            )
            is MarketDataUseCase.MarketResults.RealTimeConnectionChange -> copy(
                hasRealtimeConnection = result.hasConnection
            )
            else -> this
        }
    }

}