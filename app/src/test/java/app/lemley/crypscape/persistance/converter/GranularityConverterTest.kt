package app.lemley.crypscape.persistance.converter

import app.lemley.crypscape.persistance.entities.Granularity
import org.junit.Test

import com.google.common.truth.Truth.assertThat

class GranularityConverterTest {

    @Test
    fun converts_int__to_granularity() {
        Granularity.values().forEach {
            assertThat(GranularityConverter.toGranularity(it.seconds)).isEqualTo(it)
        }
    }

    @Test
    fun converts_granularity__to_int() {
        Granularity.values().forEach {
            assertThat(GranularityConverter.toLong(it)).isEqualTo(it.seconds)
        }
    }

}