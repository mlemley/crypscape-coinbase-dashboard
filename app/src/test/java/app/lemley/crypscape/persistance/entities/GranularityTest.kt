package app.lemley.crypscape.persistance.entities

import app.lemley.crypscape.extensions.app.persistance.fromXCoordinate
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


}