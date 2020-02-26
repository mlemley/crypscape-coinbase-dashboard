package app.lemley.crypscape.model

import app.lemley.crypscape.persistance.entities.Granularity
import app.lemley.crypscape.persistance.entities.Product
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class MarketConfigurationTest {

    @Test
    fun from_json() {
        val marketConfiguration = MarketConfiguration(platformId = 1, productRemoteId = "BTC-USD", granularity = Granularity.Hour)
        assertThat(MarketConfiguration.fromJson(marketConfiguration.toJson())).isEqualTo(
            marketConfiguration
        )
    }

    @Test
    fun serializes_to_json() {
        val marketConfiguration = MarketConfiguration(platformId = 1, productRemoteId = "BTC-USD", granularity = Granularity.Hour)

        assertThat(marketConfiguration.toJson()).isEqualTo(
            """
           {"platformId":1,"productRemoteId":"BTC-USD","granularity":"Hour"}
        """.trimIndent()
        )
    }


}