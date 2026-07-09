package com.example.sentinal

import android.app.Application
import androidx.room.Room
import com.example.sentinal.data.CameraRepository
import com.example.sentinal.data.SentinelDatabase
import com.example.sentinal.utils.NotificationHelper

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

    override fun onCreate() {
        super.onCreate()
        NotificationHelper.createNotificationChannel(this)
    }
}