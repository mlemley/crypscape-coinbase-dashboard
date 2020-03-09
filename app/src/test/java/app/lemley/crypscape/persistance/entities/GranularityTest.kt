package app.lemley.crypscape.persistance.entities

import app.lemley.crypscape.extensions.app.persistance.fromXCoordinate
import app.lemley.crypscape.extensions.app.persistance.periodForTickerTime
import app.lemley.crypscape.extensions.app.persistance.toXCoordinate
import app.lemley.crypscape.extensions.toEpochMinute
import app.lemley.crypscape.extensions.toInstant
import app.lemley.crypscape.extensions.utc
import org.junit.Test

import com.google.common.truth.Truth.assertThat

class GranularityTest {

    @Test
    fun scale_minute() {
        //2020/02/27@21:58CST
        val utc1 = 1582862280000.toInstant()
        val toXCoordinate1 = Granularity.Minute.toXCoordinate(utc1)
        //2020/02/27@21:59CST
        val utc2 = 1582862340000.toInstant()
        val toXCoordinate2 = Granularity.Minute.toXCoordinate(utc2)
        println(toXCoordinate1)
        println(toXCoordinate2)
        assertThat(toXCoordinate2 - toXCoordinate1).isEqualTo(1F)
        assertThat(Granularity.Minute.fromXCoordinate(toXCoordinate1).toEpochMilli()).isEqualTo(utc1.toEpochMilli())
    }

    @Test
    fun scale_hour() {
        //2020/02/27@20:00CST
        val utc1 = 1582855200000.toInstant()
        val toXCoordinate1 = Granularity.Hour.toXCoordinate(utc1)
        //2020/02/27@21:00CST
        val utc2 = 1582858800000.toInstant()
        val toXCoordinate2 = Granularity.Hour.toXCoordinate(utc2)
        println(toXCoordinate1)
        println(toXCoordinate2)
        assertThat(toXCoordinate2 - toXCoordinate1).isEqualTo(1F)
        assertThat(Granularity.Hour.fromXCoordinate(toXCoordinate1).toEpochMilli()).isEqualTo(utc1.toEpochMilli())
    }


    @Test
    fun calculates_period_for_given_time() {
        val time = "2020-03-06T05:59:52.271453Z"
        assertThat(Granularity.Minute.periodForTickerTime(time).toEpochMilli()).isEqualTo(1583474340000)
        assertThat(Granularity.FiveMinutes.periodForTickerTime(time).toEpochMilli()).isEqualTo(1583474100000)
        assertThat(Granularity.FifteenMinutes.periodForTickerTime(time).toEpochMilli()).isEqualTo(1583473500000)
        assertThat(Granularity.Hour.periodForTickerTime(time).toEpochMilli()).isEqualTo(1583470800000)
        assertThat(Granularity.SixHours.periodForTickerTime(time).toEpochMilli()).isEqualTo(1583452800000)
        assertThat(Granularity.Day.periodForTickerTime(time).toEpochMilli()).isEqualTo(1583452800000)

    }
}