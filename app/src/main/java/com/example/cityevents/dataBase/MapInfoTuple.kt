package com.example.cityevents.dataBase

import com.mapbox.maps.QueriedFeature

data class MapInfoTuple(
    val id: Long,
    val mapH3Id: String,
    val mapCellInternalId: String
)
