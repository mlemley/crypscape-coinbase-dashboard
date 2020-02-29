package app.lemley.crypscape.ui.market

import app.lemley.crypscape.client.coinbase.model.Ticker
import app.lemley.crypscape.persistance.entities.Granularity
import app.lemley.crypscape.ui.base.Event

sealed class MarketEvents: Event {
    object Init : MarketEvents()
    data class GranularitySelected(val granularity: Granularity) : MarketEvents()
    data class TickerChangedEvent(val ticker: Ticker) : MarketEvents()
}