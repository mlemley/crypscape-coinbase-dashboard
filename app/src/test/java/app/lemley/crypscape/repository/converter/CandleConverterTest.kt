package app.lemley.crypscape.repository.converter

import app.lemley.crypscape.client.coinbase.model.Granularity as CbGranularity
import app.lemley.crypscape.extensions.toInstant
import app.lemley.crypscape.persistance.entities.Candle
import app.lemley.crypscape.persistance.entities.Granularity
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class CandleConverterTest {

    @Test
    fun converts_candle_array___to_persistable_candle() {
        val candle = arrayOf<Double>(1415398768.0, 0.32, 4.2, 0.35, 4.2, 12.3)
        assertThat(
            CandleConverter().convert(
                platformId = 1,
                productId = 2,
                granularity=Granularity.Hour,
                candle = candle
            )
        ).isEqualTo(
            Candle(
                platform_id = 1,
                product_id = 2,
                granularity = Granularity.Hour,
                time = candle[0].toInstant(),
                low = candle[1],
                high = candle[2],
                open = candle[3],
                close = candle[4],
                volume = candle[5]
            )
        )
    }


}