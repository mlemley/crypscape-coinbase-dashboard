package app.lemley.crypscape.ui.market

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import app.lemley.crypscape.client.coinbase.CoinBaseWSService
import app.lemley.crypscape.client.coinbase.model.Subscribe
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
            coinBaseWSService.observeTicker().consumeAsFlow()
                .filter {
                    it.productId == productId
                }
                .onEach {
                    dispatchEvent(MarketEvents.TickerChangedEvent(it))
                }
                .collect()
        }
    }

    // Consider pushing data onto state rather than it's own live data
    private var productId: String? = null
        set(value) {
            value?.let {
                unsubscribeFromProduct(it)
            }
            field = value
            value?.let {
                subscribeToProduct(it)
            }
        }

    private fun unsubscribeFromProduct(it: String) {
        coinBaseWSService.sendSubscribe(
            subscriptionFor(
                Subscribe.Type.Unsubscribe,
                listOf(it),
                listOf(Subscribe.Channel.Ticker)
            )
        )
    }

    private fun subscribeToProduct(it: String) {
        coinBaseWSService.sendSubscribe(
            subscriptionFor(
                Subscribe.Type.Subscribe,
                listOf(it),
                listOf(Subscribe.Channel.Ticker)
            )
        )
    }

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
                is MarketEvents.Init -> emit(MarketActions.FetchMarketDataForDefaultConfiguration)
                is MarketEvents.GranularitySelected -> emit(MarketActions.OnGranularityChanged(it.granularity))
                is MarketEvents.TickerChangedEvent -> emit(MarketActions.OnTickerTick(it.ticker))
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
            else -> this
        }
    }

}