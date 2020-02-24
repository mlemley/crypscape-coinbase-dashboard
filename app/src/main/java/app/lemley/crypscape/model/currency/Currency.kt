package app.lemley.crypscape.model.currency

import com.crypscape.currencies.UsdCurrency
import java.math.BigDecimal

interface Currency {
    val symbol: String

    fun toFormattedCurrency(): String

    fun update(value: BigDecimal)

    class Builder {
        private var currency: String = ""
        private var value: BigDecimal = BigDecimal.valueOf(0)

        fun setCurrency(currency: String): Builder {
            this.currency = currency.toUpperCase()
            return this
        }

        fun setValue(value: Double): Builder {
            this.value = BigDecimal.valueOf(value)
            return this
        }

        fun setValue(value: BigDecimal): Builder {
            this.value = value
            return this
        }

        fun build(): Currency {
            return if ("USD" == currency)
                UsdCurrency(value)
            else
                CryptoCurrency(value)
        }

        fun setProduct(product: String): Builder {
            currency = product.split("-".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()[1]
            return this
        }
    }
}
