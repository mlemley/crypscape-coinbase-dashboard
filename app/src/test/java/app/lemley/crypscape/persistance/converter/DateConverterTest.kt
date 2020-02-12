package app.lemley.crypscape.persistance.converter

import app.lemley.crypscape.extensions.toInstant
import com.crypscape.mobile.db.converter.DateConverter
import org.junit.Test

import com.google.common.truth.Truth.assertThat

class DateConverterTest {

    @Test
    fun converts_to_date() {
        assertThat(DateConverter.toDate(1581529276000).toEpochMilli()).isEqualTo(1581529276000)
    }

    @Test
    fun converts_to_milliseconds() {
        assertThat(DateConverter.toMilliseconds(1581529276000.toInstant())).isEqualTo(1581529276000)
    }
}