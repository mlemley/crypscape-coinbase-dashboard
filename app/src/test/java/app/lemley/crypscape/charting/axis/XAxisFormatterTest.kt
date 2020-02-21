package app.lemley.crypscape.charting.axis

import app.lemley.crypscape.persistance.entities.Granularity
import com.google.common.truth.Truth.assertThat
import io.mockk.mockk
import org.junit.Test

class XAxisFormatterTest {

    @Test
    fun formats_axis() {
        assertThat(XAxisFormatter(Granularity.Hour).getFormattedValue(1_000.00F, mockk())).isEqualTo("1000")
    }

}