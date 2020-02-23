package app.lemley.crypscape.repository

import app.lemley.crypscape.client.coinbase.CoinBaseApiClient
import app.lemley.crypscape.client.coinbase.model.Ticker

class CoinBaseTickerRepository constructor(
   val coinBaseApiClient: CoinBaseApiClient
) {

    suspend fun tickerFor(serverId:String):Ticker? {
        return null
    }
}
