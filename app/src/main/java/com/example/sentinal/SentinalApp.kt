package com.example.sentinal

import android.app.Application
import androidx.room.Room
import com.example.sentinal.data.CameraRepository
import com.example.sentinal.data.SentinelDatabase

class SentinalApp: Application() {
    val database by lazy {
        Room.databaseBuilder(
            this,
            SentinelDatabase::class.java,
            "sentinel_database"
        ).build()
    }
    val repository by lazy {
        CameraRepository(database.cameraDao())
    }
}