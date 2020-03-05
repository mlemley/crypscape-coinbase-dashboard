package app.lemley.crypscape.client.coinbase.model

import org.junit.Test

import com.google.common.truth.Truth.assertThat

class TickerTest {

    @Test
    fun calculates_percentage_change() {
        assertThat(Ticker(price = 45.5, open24h = 35.0).dailyPercentageChange).isEqualTo(30.00)
        assertThat(Ticker(price = 35.0, open24h = 45.5).dailyPercentageChange).isEqualTo(-23.08)
    }


}