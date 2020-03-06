package app.lemley.crypscape.ui.market

import app.lemley.crypscape.client.coinbase.model.Ticker
import app.lemley.crypscape.persistance.entities.Granularity
import app.lemley.crypscape.ui.base.Event
import app.lemley.crypscape.ui.base.Result

sealed class MarketEvents: Event {
    object Init : MarketEvents()
    data class GranularitySelected(val granularity: Granularity) : MarketEvents()
    data class TickerChangedEvent(val ticker: Ticker) : MarketEvents()
    data class RealtimeConnectionChangedEvent(val hasConnection:Boolean) : MarketEvents()
}

sealed class MarketResults :Result {
    data class HasRealTimeConnectionResult(val hasConnection:Boolean) : MarketResults()

}