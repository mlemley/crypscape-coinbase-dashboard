package app.lemley.crypscape.ui.market

import app.lemley.crypscape.ui.base.Event

sealed class MarketEvents: Event {
    object Init : MarketEvents()
}