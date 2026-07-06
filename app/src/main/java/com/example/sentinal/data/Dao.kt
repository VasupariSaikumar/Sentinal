package com.example.sentinal.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CameraDao{
    @Insert
    suspend fun insertCamera(camera : CameraEntity)

    @Query("SELECT * FROM cameras")//get all things from "cameras"
    fun getAllCameras(): Flow<List<CameraEntity>>
    //Flow means it automatically updates the UI when data updates/changes
    @Delete
    suspend fun deleteCamera(camera: CameraEntity)

    @Query("SELECT * FROM cameras WHERE  id = :cameraId")
    fun getCameraById(cameraId: Int): Flow<CameraEntity?>
}
