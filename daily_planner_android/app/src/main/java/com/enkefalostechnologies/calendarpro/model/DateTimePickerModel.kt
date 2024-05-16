package com.enkefalostechnologies.calendarpro.model

import java.util.Date

data class DateTimePickerModel(
    var isDateSelected: Boolean? = false,
    var isTimeSelected: Boolean? = false,
    var dateTime: Date
)