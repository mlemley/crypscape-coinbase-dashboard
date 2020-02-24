package app.lemley.crypscape.model.currency

import com.crypscape.currencies.UsdCurrency
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.math.BigDecimal

class UsdCurrencyTest {

    @Test
    fun test_currency() {
        val currency = UsdCurrency(12345.678)
        assertThat(currency.toFormattedCurrency()).isEqualTo("$12,345.68")
    }

    @Test
    fun can_be_updated() {
        val value = 12f
        val currency = UsdCurrency(12345.678)
        currency.update(BigDecimal.valueOf(value.toDouble()))
        assertThat(currency.toFormattedCurrency()).isEqualTo("$12.00")
    }

    @Test
    fun float_to_usd() {
        assertThat(12.00F.toUsd()).isEqualTo(UsdCurrency(12.00))
    }

    @Test
    fun double_to_usd() {
        assertThat(12.00.toUsd()).isEqualTo(UsdCurrency(12.00))
    }

    @Test
    fun int_to_usd() {
        assertThat(12_00.toUsd()).isEqualTo(UsdCurrency(12.00))
    }

    @Test
    fun long_to_usd() {
        assertThat(12_00L.toUsd()).isEqualTo(UsdCurrency(12.00))
    }
}