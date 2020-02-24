package app.lemley.crypscape.model.currency

import com.crypscape.currencies.UsdCurrency
import java.math.BigDecimal

class CryptoCurrency : BaseCurrency, Currency {
    fun toUsd(price: Double): Double {
        return UsdCurrency(price * value.toDouble()).toDouble()
    }

    override val numberOfDecimalPlaces: Int get() = 8
    override  val currencyFormat: String get() = "#,##0.########"
    override val symbol:String  get() = ""

    constructor(doubleValue: Double) : super(doubleValue)
    constructor(value: BigDecimal) : super(value)
}
