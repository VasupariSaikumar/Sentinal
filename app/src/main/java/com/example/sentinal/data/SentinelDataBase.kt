package com.example.sentinal.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [CameraEntity::class] , version = 1)
abstract class SentinelDatabase : RoomDatabase() { // creating a blueprint for the database
    abstract fun cameraDao(): CameraDao // connects to dao to access or delete
}
