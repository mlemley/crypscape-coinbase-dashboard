package app.lemley.crypscape.charting.axis

import org.junit.Test

import com.google.common.truth.Truth.assertThat
import io.mockk.mockk

class YAxisFormatterTest {

    @Test
    fun formats_axis() {
        assertThat(
            YAxisFormatter().getFormattedValue(1_000.00F, mockk())
        ).isEqualTo("1,000")
    }
}