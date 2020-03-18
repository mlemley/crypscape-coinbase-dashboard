package app.lemley.crypscape.client.coinbase

import app.lemley.crypscape.client.coinbase.model.OrderBook
import app.lemley.crypscape.client.coinbase.model.Subscribe
import app.lemley.crypscape.client.coinbase.model.Ticker
import com.tinder.scarlet.WebSocket
import com.tinder.scarlet.ws.Receive
import com.tinder.scarlet.ws.Send
import kotlinx.coroutines.channels.ReceiveChannel

interface CoinBaseWSService {
    @Send
    fun sendSubscribe(subscribe: Subscribe)

    @Receive
    fun observeWebSocketEvent(): ReceiveChannel<WebSocket.Event>

    @Receive
    fun observeTicker(): ReceiveChannel<Ticker>

    @Receive
    fun observeOrderBook(): ReceiveChannel<OrderBook>
}