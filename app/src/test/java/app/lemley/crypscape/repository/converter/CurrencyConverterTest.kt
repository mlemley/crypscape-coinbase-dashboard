package app.lemley.crypscape.repository.converter

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import app.lemley.crypscape.client.coinbase.model.Currency as CBCurrency
import app.lemley.crypscape.persistance.entities.Currency as DBCurrency

class CurrencyConverterTest {

    @Test
    fun converts_coinbase_currency_to_persistable_currency() {
        val cbCurrency = CBCurrency(id = "BTC", name = "Bitcoin", minSize = 0.000000001)
        val expectedDBCurrency =
            DBCurrency(serverId = "BTC", name = "Bitcoin", baseMinSize = 0.000000001)

        assertThat(CurrencyConverter().convert(cbCurrency)).isEqualTo(expectedDBCurrency)
    }


}