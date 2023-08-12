package com.example.cityevents.data

import android.net.Uri

data class Event(
    var name: String? = null,
    var category: String? = null,
    var description: String? = null,
    var images: HashMap<String, String>? = null,
    var location: LocationSerializable? = null,
    var placeAddress: String? = null,
    var placeName: String? = null,
    var dateTime: DateTime? = null,
) : java.io.Serializable