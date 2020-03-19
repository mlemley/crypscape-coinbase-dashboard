package app.lemley.crypscape.repository

import app.lemley.crypscape.client.coinbase.CoinBaseWSService
import app.lemley.crypscape.client.coinbase.model.OrderBook
import app.lemley.crypscape.client.coinbase.model.Subscribe
import app.lemley.crypscape.client.coinbase.model.Ticker
import app.lemley.crypscape.client.coinbase.model.subscriptionFor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext


@FlowPreview
@ExperimentalCoroutinesApi
class CoinBaseRealTimeRepository constructor(
    private val coinBaseWSService: CoinBaseWSService
) {
    data class Subscriptions(val old: Subscribe? = null, val current: Subscribe? = null)
    sealed class ConnectionState {
        object Disconnected : ConnectionState()
        object Connected : ConnectionState()
    }

    val subscriptionChannel = ConflatedBroadcastChannel<Subscriptions>()
    val connectionStateChannel = ConflatedBroadcastChannel<ConnectionState>()

    val connectionStateFlow: Flow<ConnectionState>
        get() = connectionStateChannel.asFlow().flowOn(Dispatchers.IO)

    val tickerFlow: Flow<Ticker>
        get() = coinBaseWSService.observeTicker().consumeAsFlow().flowOn(Dispatchers.IO)

    val orderBookFlow: Flow<OrderBook>
        get() = coinBaseWSService.observeOrderBook().consumeAsFlow().flowOn(Dispatchers.IO)

    suspend fun subscribe(
        products: List<String>,
        channels: List<Subscribe.Channel>
    ) = withContext(Dispatchers.IO) {
        val subscriptions = subscriptionChannel.valueOrNull ?: Subscriptions()
        subscriptionChannel.offer(
            subscriptions.copy(
                old = subscriptions.current,
                current = subscriptionFor(
                    Subscribe.Type.Subscribe,
                    products,
                    channels
                )
            )
        )
    }

}