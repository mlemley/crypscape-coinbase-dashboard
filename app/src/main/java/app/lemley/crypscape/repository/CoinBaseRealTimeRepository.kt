package app.lemley.crypscape.repository

import android.util.Log
import androidx.annotation.VisibleForTesting
import app.lemley.crypscape.client.coinbase.CoinBaseWSService
import app.lemley.crypscape.client.coinbase.model.OrderBook
import app.lemley.crypscape.client.coinbase.model.Subscribe
import app.lemley.crypscape.client.coinbase.model.Ticker
import app.lemley.crypscape.client.coinbase.model.subscriptionFor
import app.lemley.crypscape.extensions.exhaustive
import com.tinder.scarlet.WebSocket
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.withContext


@FlowPreview
@ExperimentalCoroutinesApi
class CoinBaseRealTimeRepository constructor(
    private val coinBaseWSService: CoinBaseWSService
) {
    private var subscription: Subscribe? = null

    sealed class ConnectionState {
        object Disconnected : ConnectionState()
        object Connected : ConnectionState()
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    var connectionStateChannel = ConflatedBroadcastChannel<ConnectionState>()
    val connectionStateFlow: Flow<ConnectionState> get() = connectionStateChannel.asFlow()
    val tickerFlow: Flow<Ticker> get() = coinBaseWSService.observeTicker().consumeAsFlow()
    val orderBookFlow: Flow<OrderBook> get() = coinBaseWSService.observeOrderBook().consumeAsFlow()

    suspend fun subscribe(products: List<String>, channels: List<Subscribe.Channel>) =
        withContext(Dispatchers.Default) {
            subscription = subscriptionFor(
                Subscribe.Type.Subscribe,
                products,
                channels
            )

            manageConnection()
            subscription?.let {
                connectionStateFlow.collect { connectionState ->
                    when (connectionState) {
                        ConnectionState.Connected -> {
                            Log.w("--- Foo 1", "-------- subscribe 00001")
                            coinBaseWSService.sendSubscribe(it)
                        }
                        ConnectionState.Disconnected -> {
                        }
                    }.exhaustive
                }
            }

        }

    private suspend fun manageConnection() {
        coinBaseWSService.observeWebSocketEvent().consumeEach {
            when (it) {
                is WebSocket.Event.OnConnectionOpened<Any> -> {
                    subscription?.let {
                        Log.w("--- Foo 1", "-------- subscribe 00002")
                        coinBaseWSService.sendSubscribe(it)
                    }
                    if (!connectionStateChannel.isClosedForSend)
                        connectionStateChannel.offer(ConnectionState.Connected)
                }
                is WebSocket.Event.OnConnectionClosed -> {
                    if (!connectionStateChannel.isClosedForSend)
                        connectionStateChannel.offer(ConnectionState.Disconnected)
                }
                is WebSocket.Event.OnMessageReceived -> {
                    Log.v("CoinBaseRealTimeRepository", it.message.toString())
                }
            }
        }
    }

    suspend fun unsubscribe() = withContext(Dispatchers.Default) {
        subscription?.let { currentSubscription ->
            connectionStateFlow.collect { connectionState ->
                when (connectionState) {
                    ConnectionState.Connected -> {
                        coinBaseWSService.sendSubscribe(
                            currentSubscription.copy(
                                type = Subscribe.Type.Unsubscribe.toString()
                            )
                        )
                    }
                    ConnectionState.Disconnected -> {
                    }
                }.exhaustive
            }

        }
    }

}