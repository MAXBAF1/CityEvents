package com.example.cityevents.data

data class LocationSerializable(
    val latitude: Double,
    val longitude: Double
) : java.io.Serializable {
    constructor() : this(0.0, 0.0)
}