package com.example.cityevents.dataBase

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "map")
data class CellEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val mapH3Id: String,
    val mapCellInternalId: String
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CellEntity) return false

        return mapH3Id == other.mapH3Id && mapCellInternalId == other.mapCellInternalId
    }

    override fun hashCode(): Int {
        var result = mapH3Id.hashCode()
        result = 31 * result /*+ mapCellInternalId.hashCode()*/
        return result
    }
}