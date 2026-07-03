package com.example.sentinal.presentation.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.sentinal.data.CameraEntity
import com.example.sentinal.presentation.CameraViewModel

@Composable
fun AddCameraScreen(
    viewModel: CameraViewModel,
    onNavigationBack :() -> Unit
){
    var cameraName by remember { mutableStateOf("") }//assigns the entered value to variable it can be
    // changeable
    var ipAddress by remember { mutableStateOf("") }
    var port by remember {mutableStateOf("")}
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(modifier = Modifier.
    fillMaxSize()
        .padding(16.dp))
    {
        Text(text = "Add Camera" , style = MaterialTheme.typography.headlineMedium)

        OutlinedTextField(
            value = cameraName ,
            onValueChange = {cameraName = it},
            label = {Text("camera name")},
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.padding(8.dp))
        OutlinedTextField(
            value = ipAddress ,
            onValueChange = { ipAddress = it },
            label = {Text("ipAddress")},
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.padding(8.dp))
        OutlinedTextField(
            value = port ,
            onValueChange = {port = it},
            label = {Text("Enter Port ")},
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.padding(8.dp))
        OutlinedTextField(
            value = username ,
            onValueChange = {username = it},
            label = {Text("Enter username")},
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.padding(8.dp))
        OutlinedTextField(
            value = password ,
            onValueChange = {password = it},
            label = {Text("password")},
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.padding(8.dp))
        Button(onClick = {
            viewModel.insertCamera(
                CameraEntity(
                    name = cameraName ,
                    ipAddress = ipAddress ,
                    port = port.toIntOrNull(),
                    username = username ,
                    password = password
                )
            )
            onNavigationBack()
        }) {
            Text("Save camera")
        }
    }
}