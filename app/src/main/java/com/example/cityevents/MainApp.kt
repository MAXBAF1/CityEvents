package com.example.cityevents

import android.app.Application
import com.example.cityevents.dataBase.MainDb

class MainApp : Application() {
    val database by lazy { MainDb.getDatabase(this) }
}