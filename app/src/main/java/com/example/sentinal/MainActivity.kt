package com.example.sentinal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.sentinal.presentation.CameraViewModel
import com.example.sentinal.presentation.screens.AddCameraScreen
import com.example.sentinal.presentation.screens.CameraList
import com.example.sentinal.presentation.screens.LiveStreamScreen
import com.example.sentinal.ui.theme.SentinalTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SentinalTheme {
               val viewModel : CameraViewModel = viewModel()
                val navController = rememberNavController()

                NavHost(
                    navController = navController ,
                    startDestination = "camera_list"
                ){
                    composable("camera_list"){
                        CameraList(viewModel = viewModel ,
                            onAddCamera = {
                                navController.navigate("add_camera")
                            },
                            onCameraClick = {camera ->
                                navController.navigate("live_stream/${camera.id}")
                            })
                    }
                    composable("add_camera"){
                        AddCameraScreen(
                            viewModel = viewModel ,
                            onNavigationBack = {
                                navController.popBackStack()
                            }
                        )
                    }
                    composable("live_stream/{cameraId}"){backStackEntry ->
                        val cameraId = backStackEntry.arguments
                            ?.getString("cameraId")?.toIntOrNull()

                        if (cameraId == null){
                            navController.popBackStack()
                            return@composable
                        }
                        val camera by viewModel
                            .getCameraById(cameraId)
                            .collectAsState(initial = null)
                        camera?.let {
                            LiveStreamScreen(
                                camera  = it,
                                onNavigationBack = {
                                    navController.popBackStack()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
