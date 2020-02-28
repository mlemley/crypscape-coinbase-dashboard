package app.lemley.crypscape.repository

import app.lemley.crypscape.client.coinbase.CoinBaseWSService
import app.lemley.crypscape.client.coinbase.model.Subscribe
import app.lemley.crypscape.client.coinbase.model.subscriptionFor


class CoinBaseRealTimeRepository constructor(
    private val coinBaseWSService: CoinBaseWSService
) {

    fun subscribe(products: List<String>, channels: List<Subscribe.Channel>) {
        coinBaseWSService.sendSubscribe(subscriptionFor(Subscribe.Type.Subscribe, products, channels))
    }

}