package app.lemley.crypscape.model.currency

import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat

abstract class BaseCurrency(var value: BigDecimal) : Currency {
    internal abstract val numberOfDecimalPlaces: Int
    internal abstract val currencyFormat: String

    constructor(value: Double) : this(BigDecimal.valueOf(value))

    init {
        scaleValue(value)
    }

    override fun toFormattedCurrency(): String {
        val formatter = DecimalFormat(currencyFormat)
        return "$symbol${formatter.format(value.toDouble())}"
    }

    override fun update(value: BigDecimal) {
        scaleValue(value)
    }

    fun toDouble():Double {
        return value.toDouble()
    }

    private fun scaleValue(value: BigDecimal) {
        this.value = value.setScale(numberOfDecimalPlaces, RoundingMode.HALF_UP)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BaseCurrency

        if (value.toLong() != other.value.toLong()) return false

        return true
    }

    override fun hashCode(): Int {
        return value.toLong().hashCode()
    }
}
