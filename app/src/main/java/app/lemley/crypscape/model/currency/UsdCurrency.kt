package com.crypscape.currencies

import app.lemley.crypscape.model.currency.BaseCurrency
import app.lemley.crypscape.model.currency.Currency
import app.lemley.crypscape.model.currency.toUsd
import java.math.BigDecimal

class UsdCurrency : BaseCurrency, Currency {
    fun toCrypto(price: Double): Double {
        if (price <= 0) return 0.0
        return toDouble() / price
    }

    override val symbol: String get() = "$"
    override val numberOfDecimalPlaces: Int get() = 2
    override val currencyFormat: String get() = "#,##0.00"

    constructor(doubleValue: Double) : super(doubleValue)
    constructor(value: BigDecimal) : super(value)
    constructor(value: Long) : this(BigDecimal.valueOf(value).movePointLeft(2))

    fun copy(): UsdCurrency = value.toLong().toUsd()
}

