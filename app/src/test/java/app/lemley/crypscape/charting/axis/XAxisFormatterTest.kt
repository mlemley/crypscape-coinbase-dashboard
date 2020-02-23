package app.lemley.crypscape.charting.axis

import app.lemley.crypscape.extensions.app.persistance.toXCoordinate
import app.lemley.crypscape.extensions.toInstant
import app.lemley.crypscape.persistance.entities.Granularity
import com.google.common.truth.Truth.assertThat
import io.mockk.mockk
import org.junit.Test
import java.util.*

class XAxisFormatterTest {

    @Test
    fun formats_axis__2020_2_01_00_00_00_CST() {
        TimeZone.setDefault(SimpleTimeZone(-6, "CST"))
        val epochMilliseconds = 1580536800000
        val granularities = Granularity.values()
        val expectedLabels = listOf(
            "Feb 1",
            "Feb 1",
            "Feb 1",
            "Feb 1",
            "Feb 1",
            "Feb 1"
        )

        val actual = mutableListOf<String>()
        granularities.forEachIndexed { i, granularity ->
            actual.add(
                XAxisFormatter(granularity).getFormattedValue(
                    granularity.toXCoordinate(
                        epochMilliseconds.toInstant()
                    ), mockk()
                )
            )
        }

        assertThat(actual).isEqualTo(expectedLabels)
    }

    @Test
    fun formats_axis__2020_2_22_00_00_00_CST() {
        TimeZone.setDefault(SimpleTimeZone(-6, "CST"))
        val epochMilliseconds = 1582351200000
        val granularities = Granularity.values()
        val expectedLabels = listOf(
            "2/22",
            "2/22",
            "2/22",
            "2/22",
            "2/22",
            "2/22"
        )

        val actual = mutableListOf<String>()
        granularities.forEachIndexed { i, granularity ->
            actual.add(
                XAxisFormatter(granularity).getFormattedValue(
                    granularity.toXCoordinate(
                        epochMilliseconds.toInstant()
                    ), mockk()
                )
            )
        }

        assertThat(actual).isEqualTo(expectedLabels)
    }

    @Test
    fun formats_axis__2020_2_22_15_15_00_CST() {
        TimeZone.setDefault(SimpleTimeZone(-6, "CST"))
        val epochMilliseconds = 1582406100000
        val granularities = Granularity.values()
        val expectedLabels = listOf(
            "15:15",
            "15:15",
            "15:15",
            "15:00",
            "15:00",
            "15:00"
        )

        val actual = mutableListOf<String>()
        granularities.forEachIndexed { i, granularity ->
            actual.add(
                XAxisFormatter(granularity).getFormattedValue(
                    granularity.toXCoordinate(
                        epochMilliseconds.toInstant()
                    ), mockk()
                )
            )
        }

        assertThat(actual).isEqualTo(expectedLabels)
    }

}