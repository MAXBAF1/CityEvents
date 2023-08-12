package com.example.cityevents.data

import java.io.Serializable
import java.time.DayOfWeek

data class DateTime(
    var year: Int? = null,
    var month: Int? = null,
    var day: Int? = null,
    var hour: Int? = null,
    var minute: Int? = null,
    var dayOfWeek: String? = null
) : Serializable
