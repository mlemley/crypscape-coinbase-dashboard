package com.crypscape.mobile.db.converter

import androidx.room.TypeConverter
import app.lemley.crypscape.extensions.toInstant
import org.threeten.bp.Instant

object DateConverter {

    @TypeConverter
    @JvmStatic
    fun toDate(millis: Long): Instant = millis.toInstant()

    @TypeConverter
    @JvmStatic
    fun toMilliseconds(instant: Instant): Long = instant.toEpochMilli()

}
