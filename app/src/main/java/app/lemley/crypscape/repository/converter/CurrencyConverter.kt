package app.lemley.crypscape.repository.converter

import app.lemley.crypscape.persistance.entities.Currency
import app.lemley.crypscape.client.coinbase.model.Currency as CBCurrency

class CurrencyConverter {

    fun convert(coinBaseCurrency: CBCurrency): Currency {
        return Currency(
            serverId = coinBaseCurrency.id,
            name = coinBaseCurrency.name,
            baseMinSize = coinBaseCurrency.minSize
        )
    }
}
