package app.lemley.crypscape.extensions

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.util.*

class DatesKtxTest {

    @Test
    fun convert_date_strings_to_instances() {
        assertThat("2020-02-12T17:41:16Z".toInstant()?.toEpochMilli()).isEqualTo(1581529276000)
        assertThat("2020-02-12T17:41:16.000000Z".toInstant()?.toEpochMilli()).isEqualTo(
            1581529276000
        )
    }

    @Test
    fun converts_timestamp_to_local_time() {
        TimeZone.setDefault(SimpleTimeZone(-6, "CST"))

        val localtime = "2020-02-12T17:41:16Z".toLocalTime()!!

        assertThat(localtime.year).isEqualTo(2020)
        assertThat(localtime.monthValue).isEqualTo(2)
        assertThat(localtime.dayOfMonth).isEqualTo(12)
        assertThat(localtime.hour).isEqualTo(11)
        assertThat(localtime.minute).isEqualTo(41)
        assertThat(localtime.second).isEqualTo(16)
    }

    @Test
    fun converts_timestamp_to_local_time__back_to_millis() {
        TimeZone.setDefault(SimpleTimeZone(-6, "CST"))

        val localtime = "2020-02-12T17:41:16Z".toLocalTime()!!

        assertThat(localtime.toMillis()).isEqualTo(1581529276000)
    }

    @Test
    fun converts_millis_to_localized_time() {
        TimeZone.setDefault(SimpleTimeZone(-6, "CST"))
        val localtime = "2020-02-12T17:41:16Z".toLocalTime()!!
        val millis = localtime.toMillis()

        val actual = millis.toLocalTime()

        assertThat(actual.year).isEqualTo(2020)
        assertThat(actual.monthValue).isEqualTo(2)
        assertThat(actual.dayOfMonth).isEqualTo(12)
        assertThat(actual.hour).isEqualTo(11)
        assertThat(actual.minute).isEqualTo(41)
        assertThat(actual.second).isEqualTo(16)
    }

    @Test
    fun nanos_to_local_time() {
        val localTime = 1581529276000.toLocalTime()

        assertThat(localTime.year).isEqualTo(2020)
        assertThat(localTime.monthValue).isEqualTo(2)
        assertThat(localTime.dayOfMonth).isEqualTo(12)
        assertThat(localTime.hour).isEqualTo(11)
        assertThat(localTime.minute).isEqualTo(41)
        assertThat(localTime.second).isEqualTo(16)
    }

    @Test
    fun creates__iso8061_compliant_timestamp() {
        assertThat(1581529276000.toIso8601TimeStamp()).isEqualTo("2020-02-12T17:41:16Z")
    }

    @Test
    fun creates__iso8061_compliant_timestamp__seconds_truncated() {
        assertThat(1581529276000.toIso8601TimeStampToMinute()).isEqualTo("2020-02-12T17:41Z")
    }

    @Test
    fun converts__double__to__instant() {
        assertThat(1581529276.000F.toInstant().toEpochMilli()).isEqualTo(1581529216000)
    }

    @Test
    fun converts__float__to__instant() {
        assertThat(1581529276.000F.toInstant().toEpochMilli()).isEqualTo(1581529216000)
    }
}