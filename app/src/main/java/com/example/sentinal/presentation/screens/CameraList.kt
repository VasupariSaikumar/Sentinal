package com.example.sentinal.presentation.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.sentinal.data.CameraEntity
import com.example.sentinal.presentation.CameraViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraList(
    viewModel: CameraViewModel,
    onAddCamera : () -> Unit,
    onCameraClick: (CameraEntity) -> Unit
) {
    val cameras by viewModel.allCameras.collectAsState()
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Sentinel") }
            )
        }, floatingActionButton = {
            FloatingActionButton( onClick = {
                onAddCamera()
            }) {
                Icon(
                    imageVector = Icons.Default.Add , contentDescription = null
                )
            }
        }

    ) {paddingValues ->
        if (cameras.isEmpty()){
            Box(modifier = Modifier.fillMaxSize()
                .padding(paddingValues),
                contentAlignment = Alignment.Center){
                Text("NO camera added yet !\n Tap + to add one ")
            }
        }else{
            LazyColumn(
                modifier = Modifier.fillMaxSize()
                    .padding(paddingValues)
            ) {
                items(cameras){camera ->
                    CameraCard(camera = camera,
                        onCameraClick = {clickedCamera ->
                            onCameraClick(clickedCamera)
                        })
                }
            }
        }
    }
}
@Composable
fun CameraCard(camera: CameraEntity,
               onCameraClick : (CameraEntity) -> Unit){
    Card(modifier = Modifier.fillMaxWidth()
        .padding(horizontal = 16.dp , vertical = 8.dp),
        onClick = {onCameraClick(camera)}) {
        Column(modifier = Modifier.padding(16.dp))
        {
            Text(camera.name ,style = MaterialTheme.typography.titleMedium )
            Text("${camera.ipAddress}  : +${ camera.port }", style = MaterialTheme.typography.bodyMedium  )
        }
    }
}