package com.example.cityevents.dataBase

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MapsDao {
    @Insert(entity = CellEntity::class)
    fun insertCell(mapCell: CellEntity)

    @Query("SELECT * FROM map")
    fun getAllCells(): Flow<List<CellEntity>>

    @Query("DELETE FROM map WHERE id = :mapId")
    fun deleteMapDataById(mapId: Long)

    @Query("DELETE FROM map")
    fun clear()
}