package app.lemley.crypscape.repository

import app.lemley.crypscape.client.coinbase.CoinBaseWSService
import app.lemley.crypscape.client.coinbase.model.Subscribe
import app.lemley.crypscape.client.coinbase.model.Ticker
import app.lemley.crypscape.client.coinbase.model.subscriptionFor
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.consumeAsFlow


@FlowPreview
class CoinBaseRealTimeRepository constructor(
    private val coinBaseWSService: CoinBaseWSService
) {

    val tickerFlow: Flow<Ticker> get() = coinBaseWSService.observeTicker().consumeAsFlow()

    fun subscribe(products: List<String>, channels: List<Subscribe.Channel>) {
        coinBaseWSService.sendSubscribe(subscriptionFor(Subscribe.Type.Subscribe, products, channels))
    }

    fun unsubscribe(products: List<String>, channels: List<Subscribe.Channel.Ticker>) {
        coinBaseWSService.sendSubscribe(subscriptionFor(Subscribe.Type.Unsubscribe, products, channels))
    }

}