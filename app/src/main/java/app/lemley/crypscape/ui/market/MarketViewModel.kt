package app.lemley.crypscape.ui.market

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import app.lemley.crypscape.client.coinbase.CoinBaseWSService
import app.lemley.crypscape.client.coinbase.model.Subscribe
import app.lemley.crypscape.client.coinbase.model.Ticker
import app.lemley.crypscape.client.coinbase.model.subscriptionFor
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
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@FlowPreview
@ExperimentalCoroutinesApi
class MarketViewModel(
    marketDataUseCase: MarketDataUseCase,
    coinBaseRepository: CoinBaseRepository,
    val coinBaseWSService: CoinBaseWSService
) : BaseViewModel<MarketEvents, MarketState>() {

    init {
        viewModelScope.launch {
            coinBaseWSService.observeTicker().consumeEach {
                tickerChannel.offer(it)
                //dispatchEvent()
            }
        }
    }

    // Consider pushing data onto state rather than it's own live data
    private var productId: String? = null
        set(value) {
            value?.let {
                coinBaseWSService.sendSubscribe(
                    subscriptionFor(
                        Subscribe.Type.Unsubscribe,
                        listOf(it),
                        listOf(Subscribe.Channel.Ticker)
                    )
                )
            }
            field = value
            value?.let {
                coinBaseWSService.sendSubscribe(
                    subscriptionFor(
                        Subscribe.Type.Subscribe,
                        listOf(it),
                        listOf(Subscribe.Channel.Ticker)
                    )
                )
            }
        }

    class CandleFilter(val marketConfiguration: MarketConfiguration)

    private val candleChannel = ConflatedBroadcastChannel<CandleFilter>()
    val candles: LiveData<List<Candle>> = candleChannel.asFlow()
        .flatMapLatest { filter ->
            coinBaseRepository.candlesForConfiguration(filter.marketConfiguration)

        }
        .flowOn(Dispatchers.IO)
        .asLiveData()

    private val tickerChannel = ConflatedBroadcastChannel<Ticker>()
    val ticker: LiveData<Ticker> = tickerChannel.asFlow()
        .conflate()
        .distinctUntilChanged()
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
                productId = result.marketConfiguration.productRemoteId
            }

            is MarketDataUseCase.MarketResults.TickerResult -> copy(
                ticker = result.ticker
            ).also {
                tickerChannel.offer(result.ticker)
            }
            else -> this
        }
    }

}