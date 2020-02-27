package app.lemley.crypscape.usecase

import android.util.Log
import app.lemley.crypscape.client.coinbase.CoinBaseWSService
import app.lemley.crypscape.client.coinbase.model.Subscribe
import app.lemley.crypscape.client.coinbase.model.Ticker
import app.lemley.crypscape.client.coinbase.model.subscriptionFor
import app.lemley.crypscape.extensions.exhaustive
import app.lemley.crypscape.model.MarketConfiguration
import app.lemley.crypscape.persistance.entities.Granularity
import app.lemley.crypscape.repository.CoinBaseRepository
import app.lemley.crypscape.repository.DefaultMarketDataRepository
import app.lemley.crypscape.ui.base.Action
import app.lemley.crypscape.ui.base.Result
import com.tinder.scarlet.WebSocket
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOn

@ExperimentalCoroutinesApi
class MarketDataUseCase constructor(
    val defaultMarketDataRepository: DefaultMarketDataRepository,
    val coinBaseRepository: CoinBaseRepository,
    val coinBaseWSService: CoinBaseWSService
) : UseCase {

    sealed class MarketActions : Action {
        object FetchMarketDataForDefaultConfiguration : MarketActions()
        data class OnGranularityChanged(val granularity: Granularity) : MarketActions()

    }

    sealed class MarketResults : Result {
        data class MarketConfigurationResult(val marketConfiguration: MarketConfiguration) :
            MarketResults()

        data class TickerResult(val ticker: Ticker) : MarketResults()
    }

    override fun canProcess(action: Action): Boolean = action is MarketActions

    override fun handleAction(action: Action): Flow<Result> {
        return when (action) {
            is MarketActions.FetchMarketDataForDefaultConfiguration -> handleFetchDefaultMarketData()
            is MarketActions.OnGranularityChanged -> handleOnGranularityChanged(action.granularity)
            else -> emptyFlow()
        }.exhaustive
    }

    private fun handleOnGranularityChanged(granularity: Granularity): Flow<Result> =
        channelFlow<Result> {
            val marketConfiguration = defaultMarketDataRepository.changeGranularity(granularity)
            send(MarketResults.MarketConfigurationResult(marketConfiguration))
            coinBaseRepository.tickerForConfiguration(marketConfiguration)?.let {
                send(MarketResults.TickerResult(it))
            }
            val subscribeMessage = subscriptionFor(
                type = Subscribe.Type.Subscribe,
                products = listOf("BTC-USD"),
                channels = listOf(Subscribe.Channel.Ticker)
            )

            coinBaseWSService.observeWebSocketEvent()
                .consumeEach {
                    when (it) {
                        is WebSocket.Event.OnConnectionOpened<*> -> {
                            Log.w("--websocket--", "connection opened")
                            coinBaseWSService.sendSubscribe(subscribeMessage)
                            Log.w("--websocket--", "subscribe")
                            Log.w("--websocket--", "observe ticker changes")
                            coinBaseWSService.observeTicker().consumeEach { ticker ->
                                Log.e(
                                    "-- websocket --",
                                    "Bitcoin price is ${ticker.price} at ${ticker.time}"
                                )

                                send(MarketResults.TickerResult(ticker))
                            }
                        }
                    }
                }
        }.flowOn(Dispatchers.IO)

    private fun handleFetchDefaultMarketData(): Flow<Result> = channelFlow<Result> {
        val marketConfiguration = defaultMarketDataRepository.loadDefault()
        send(MarketResults.MarketConfigurationResult(marketConfiguration))
        coinBaseRepository.tickerForConfiguration(marketConfiguration)?.let {
            send(MarketResults.TickerResult(it))
        }
    }.flowOn(Dispatchers.IO)
}
