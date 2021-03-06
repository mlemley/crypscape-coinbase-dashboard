package app.lemley.crypscape.ui.market

import app.lemley.crypscape.client.coinbase.model.Ticker
import app.lemley.crypscape.model.MarketConfiguration
import app.lemley.crypscape.persistance.entities.Candle
import app.lemley.crypscape.ui.base.State
import kotlinx.coroutines.flow.Flow


data class MarketState(
    val marketConfiguration: MarketConfiguration? = null,
    val candles: List<Candle> = emptyList(),
    val ticker:Ticker? = null,
    val hasRealtimeConnection:Boolean = false
): State