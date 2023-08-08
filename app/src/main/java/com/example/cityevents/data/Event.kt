package com.example.cityevents.data

import java.time.LocalDateTime

data class Event(
    val images: List<String>,
    val category: String,
    val name: String,
    val date: LocalDateTime,
    val placeAddress: String,
    val placeName: String,
    val description: String
)
