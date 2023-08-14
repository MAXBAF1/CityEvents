package com.example.cityevents.data

import java.util.ArrayList

data class Event(
    var name: String? = null,
    var category: String? = null,
    var description: String? = null,
    var images: ArrayList<String>? = null,
    var location: LocationSerializable? = null,
    var placeAddress: String? = null,
    var placeName: String? = null,
    var dateTime: DateTime? = null,
    var isLiked: Boolean = false
) : java.io.Serializable