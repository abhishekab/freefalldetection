package com.ab.falldetector.db

import androidx.room.TypeConverter
import java.util.*

class RoomTypeConverter {
    @TypeConverter
    fun longToDate(value: Long?): Date? {
        return if (value == null) null else Date(value)
    }

    @TypeConverter
    fun dateToLong(date: Date?): Long? {
        return date?.time
    }
}