package com.example.sentinal.data

import android.graphics.Camera
import kotlinx.coroutines.flow.Flow

class CameraRepository(private val dao : CameraDao) {
    val allCameras: Flow<List<CameraEntity>> = dao.getAllCameras()

    suspend fun insertCamera(camera : CameraEntity){
        dao.insertCamera(camera)
    }
    suspend fun deleteCamera(camera: CameraEntity) {
        dao.deleteCamera(camera)
    }
    fun getCameraById(id: Int): Flow<CameraEntity?>{
        return dao.getCameraById(id)
    }
}
/*
Repository is a middle layer
This lies in between Dao and UI
Purpose of the layer is to access the dao methods using an api without totally exposing Dao methods
 */