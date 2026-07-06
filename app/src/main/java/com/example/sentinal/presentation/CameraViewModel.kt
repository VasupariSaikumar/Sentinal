package com.example.sentinal.presentation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.sentinal.SentinalApp
import com.example.sentinal.data.CameraEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CameraViewModel(application: Application): AndroidViewModel(application) {
    //Application(prebuilt class) has total app context and application inherits it
    //AndroidViewModel
    private val repository = (application as SentinalApp).repository
    //.repository gives CameraRepository object
    val allCameras: StateFlow<List<CameraEntity>> = repository.allCameras
        .stateIn(//converting flow into state flow
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    fun insertCamera(camera: CameraEntity){
        viewModelScope.launch {
            repository.insertCamera(camera)
        }
    }
    fun deleteCamera(camera: CameraEntity) {
        viewModelScope.launch {
            repository.deleteCamera(camera)
        }
    }
    fun getCameraById(id:Int): Flow<CameraEntity?> {
        return repository.getCameraById(id)
    }
}