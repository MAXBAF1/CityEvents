package com.example.cityevents.data

data class Event(
    val name: String,
    val category: String,
    val description: String,
    var images: List<String>? = null,
    var location: LocationSerializable? = null,
    var placeAddress: String? = null,
    var placeName: String? = null,
    var dateTime: DateTime? = null,
) : java.io.Serializable
