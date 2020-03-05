package app.lemley.crypscape.repository

import app.lemley.crypscape.client.coinbase.CoinBaseApi
import app.lemley.crypscape.client.coinbase.model.Ticker

class CoinBaseTickerRepository constructor(
    val coinBaseApi: CoinBaseApi
) {

    suspend fun tickerFor(productId: String): Ticker {
        return coinBaseApi.tickerFor(productId)
    }
}
