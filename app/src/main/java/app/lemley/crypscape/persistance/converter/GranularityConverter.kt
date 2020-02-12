package app.lemley.crypscape.persistance.converter

import androidx.room.TypeConverter
import app.lemley.crypscape.persistance.entities.Granularity

object GranularityConverter {

    @TypeConverter
    @JvmStatic
    fun toGranularity(seconds: Long): Granularity = Granularity.fromSeconds(seconds)

    @TypeConverter
    @JvmStatic
    fun toLong(granularity: Granularity): Long = granularity.seconds

}