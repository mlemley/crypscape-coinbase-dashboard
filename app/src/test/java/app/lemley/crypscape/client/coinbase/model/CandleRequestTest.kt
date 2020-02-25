package app.lemley.crypscape.client.coinbase.model

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class CandleRequestTest {

    @Test
    fun returns_as_map__given_start_end_and_granularity() {
        val start = "2018-12-26T12:20:00.000Z"
        val end = "2018-12-26T17:20:00.000Z"
        val granularity = Granularity.FiveMinutes
        assertThat(
            CandleRequest(
                productId = "BTC-USD",
                granularity = Granularity.FiveMinutes,
                start = start,
                end = end
            ).asMap()
        ).isEqualTo(
            mapOf(
                Pair("granularity", granularity.seconds.toString()),
                Pair("start", start),
                Pair("end", end)
            )
        )
    }

    @Test
    fun returns_as_map__granularity_only__when_end_missing() {
        val start = "2018-12-26T12:20:00.000Z"
        val granularity = Granularity.FiveMinutes
        assertThat(
            CandleRequest(
                productId = "BTC-USD",
                granularity = Granularity.FiveMinutes,
                start = start
            ).asMap()
        ).isEqualTo(
            mapOf(
                Pair("granularity", granularity.seconds.toString())
            )
        )
    }

    @Test
    fun returns_as_map__granularity_only__when_start_missing() {
        val end = "2018-12-26T17:20:00.000Z"
        val granularity = Granularity.FiveMinutes
        assertThat(
            CandleRequest(
                productId = "BTC-USD",
                granularity = Granularity.FiveMinutes,
                end = end
            ).asMap()
        ).isEqualTo(
            mapOf(
                Pair("granularity", granularity.seconds.toString())
            )
        )
    }

    @Test
    fun returns_as_map__granularity_only() {
        val granularity = Granularity.FiveMinutes
        assertThat(
            CandleRequest(
                productId = "BTC-USD",
                granularity = Granularity.FiveMinutes
            ).asMap()
        ).isEqualTo(
            mapOf(
                Pair("granularity", granularity.seconds.toString())
            )
        )
    }

    @Test
    fun populates__end__start__with_current_time_minus__300_candles_minute() {

        val granularity = Granularity.FiveMinutes
        assertThat(
            CandleRequest(
                productId = "BTC-USD",
                granularity = Granularity.FiveMinutes
            ).asMap()
        ).isEqualTo(
            mapOf(
                Pair("granularity", granularity.seconds.toString())
            )
        )
    }
}