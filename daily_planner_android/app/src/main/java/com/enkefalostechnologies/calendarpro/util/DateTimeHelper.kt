package com.enkefalostechnologies.calendarpro.util

import java.util.Calendar
import java.util.Date

object  DateTimeHelper {
    fun Date.minuteBefore(minute:Int): Date {
        val calendar = Calendar.getInstance()
        calendar.time = this
        calendar.add(Calendar.MINUTE, -minute)
        return calendar.time
    }
}