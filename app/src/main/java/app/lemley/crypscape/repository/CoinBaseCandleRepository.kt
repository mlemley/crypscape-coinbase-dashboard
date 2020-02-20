package app.lemley.crypscape.repository

import app.lemley.crypscape.client.coinbase.CoinBaseApiClient
import app.lemley.crypscape.persistance.entities.Candle
import app.lemley.crypscape.persistance.entities.Granularity

class CoinBaseCandleRepository constructor(val coinBaseApiClient: CoinBaseApiClient) {

    suspend fun candlesFor(serverId: String, granularity: Granularity): List<Candle> {
        return emptyList()
    }
}
