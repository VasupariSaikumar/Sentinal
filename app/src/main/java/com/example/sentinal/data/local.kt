package com.example.sentinal.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cameras")
data class CameraEntity(
    @PrimaryKey(autoGenerate = true) val id : Int,
    val ipAddress :String ,
    val port : Int,
    val username:String,
    val password: String
)
//So CameraEntity describes what one camera looks like in storage.
// Every camera you add becomes one row in that table.