package com.example.cityevents.data

import android.net.Uri

data class EventInternet(
    var name: String? = null,
    var category: String? = null,
    var description: String? = null,
    var images: List<String>? = null,
    var location: LocationSerializable? = null,
    var placeAddress: String? = null,
    var placeName: String? = null,
    var dateTime: DateTime? = null,
    var isLiked: Boolean = false
) : java.io.Serializable {
    constructor(event: Event) : this(
        event.name,
        event.category,
        event.description,
        event.images?.values?.toList(),
        event.location,
        event.placeAddress,
        event.placeName,
        event.dateTime,
        event.isLiked
    )
}