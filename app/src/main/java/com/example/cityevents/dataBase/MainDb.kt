package com.example.cityevents.dataBase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    version = 2,
    entities = [
        CellEntity::class,
    ]
)
abstract class MainDb : RoomDatabase() {
    abstract fun getDao(): MapsDao
    companion object {
        @Volatile
        private var INSTANCE: MainDb? = null
        fun getDatabase(context: Context): MainDb{
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext, MainDb :: class.java,"Around.db").build()

                INSTANCE = instance
                return instance
            }
        }
    }
}

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE map ADD COLUMN mapH3Id STRING")
    }
}