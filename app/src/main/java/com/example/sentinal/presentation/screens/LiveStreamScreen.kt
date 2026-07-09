package com.example.sentinal.presentation.screens

import android.util.Log
import android.view.LayoutInflater
import android.view.TextureView
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.example.sentinal.R
import com.example.sentinal.data.CameraEntity
import com.example.sentinal.ml.MotionDetector
import com.example.sentinal.utils.NotificationHelper
import kotlinx.coroutines.delay

@OptIn(UnstableApi::class)
@Composable
fun LiveStreamScreen(
    camera: CameraEntity,
    onNavigationBack : () -> Unit
){
    val context = LocalContext.current // i know about context it just know the current condition of the app but y we need in this file

  //  val rtspUrl = "rtsp://wowzaec2demo.streamlock.net/vod/mp4:BigBuckBunny_115k.mp4"
    val rtspUrl = "rtsp://${camera.username}:${camera.password}"+"@${camera.ipAddress}:${camera.port}/stream1"

    val exoPlayer = remember(context) {
        ExoPlayer.Builder(context).build().apply {
            val mediaItem = MediaItem.fromUri(rtspUrl)
            setMediaItem(mediaItem)
            prepare()
            playWhenReady = true
        }
    }
    var textureView by remember { mutableStateOf<TextureView?>(null) } // Holds reference to the TextureView once  created
    val motionDetector = remember { MotionDetector() } //Motion detector instance - survives recomposition
    var motionDetected by remember { mutableStateOf(false) } // Track motion state to show on screen

    //Capture loop
    LaunchedEffect(textureView) {
        while (true){
            delay(1500) // check for every 1.5 seconds
            val bitmap = textureView?.bitmap
            if (bitmap != null){
                val hasMotion = motionDetector.detectMotion(bitmap)
                motionDetected = hasMotion
                if(hasMotion){
                    Log.d("MotionDetector", "Motion detected!")
                    NotificationHelper.sendMotionAlert(context,  camera.name)
                }
            }
        }
    }
    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }
    Column(modifier = Modifier.fillMaxSize()) {
        //BackButton
        Button(onClick =
            onNavigationBack, modifier = Modifier.padding(8.dp)
        ) {
            Text("<-Back")
        }
        //CameraName
        Text(
            text = camera.name ,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = 16.dp )
        )
        Text(
            text = if (motionDetected)" 🔴Motion Detected!" else "🟢 no Motion",
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        AndroidView(
            factory = {ctx ->
                val playerView = LayoutInflater.from(ctx)
                    .inflate(R.layout.player_view , null) as PlayerView
                playerView.apply {
                    player = exoPlayer
                    useController = true
                }.also {
                    //capture texture view reference once created
                    textureView = it.videoSurfaceView as? TextureView
                }
            },
            modifier = Modifier.fillMaxWidth().height(250.dp)
        )
    }
}