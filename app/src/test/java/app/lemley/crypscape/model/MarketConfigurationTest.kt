package app.lemley.crypscape.model

import app.lemley.crypscape.persistance.entities.Granularity
import app.lemley.crypscape.persistance.entities.Platform
import app.lemley.crypscape.persistance.entities.Product
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import org.junit.Test

class MarketConfigurationTest {

    @Test
    fun from_json() {
        val product = Product(
            platformId = 1,
            id = 2,
            serverId = "BTC-USD",
            quoteCurrency = 3,
            baseCurrency = 4
        )

        val marketConfiguration = MarketConfiguration(product, Granularity.Hour)
        assertThat(MarketConfiguration.fromJson(marketConfiguration.toJson())).isEqualTo(
            marketConfiguration
        )
    }

    @Test
    fun serializes_to_json() {
        val product = Product(
            platformId = 1,
            id = 2,
            serverId = "BTC-USD",
            quoteCurrency = 3,
            baseCurrency = 4
        )

        assertThat(MarketConfiguration(product, Granularity.Hour).toJson()).isEqualTo(
            """
           {"product":{"id":2,"platformId":1,"baseCurrency":4,"quoteCurrency":3,"serverId":"BTC-USD","baseMinSize":0.0,"baseMaxSize":0.0,"quoteIncrement":0.0},"granularity":"Hour"}
        """.trimIndent()
        )
    }


}