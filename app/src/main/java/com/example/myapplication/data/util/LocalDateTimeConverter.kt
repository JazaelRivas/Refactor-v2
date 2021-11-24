package com.example.myapplication.data.util

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.TypeConverter
import java.time.LocalDateTime

@RequiresApi(Build.VERSION_CODES.O)
object LocalDateTimeConverter {
    @TypeConverter
    fun toDateTime(dateString: String): LocalDateTime? {
        return LocalDateTime.parse(dateString)
    }

    @TypeConverter
    fun toDateString(date: LocalDateTime): String {
        return date.toString()
    }
}