package com.example.sentinal.presentation.screens

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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.example.sentinal.data.CameraEntity

@Composable
fun LiveStreamScreen(
    camera: CameraEntity,
    onNavigationBack : () -> Unit
){
    val context = LocalContext.current // i know about context it just know the current condition of the app but y we need in this file

    val rtspUrl = "rtsp://wowzaec2demo.streamlock.net/vod/mp4:BigBuckBunny_115k.mp4"
    //val rtspUrl = "rtsp://${camera.username}:${camera.password}"+"@${camera.ipAddress}:${camera.port}/stream1"

    val exoPlayer = remember(context) {
        ExoPlayer.Builder(context).build().apply {
            val mediaItem = MediaItem.fromUri(rtspUrl)
            setMediaItem(mediaItem)
            prepare()
            playWhenReady = true
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
        AndroidView(
            factory = {ctx ->
                PlayerView(ctx).apply{
                    player = exoPlayer
                    useController = true
                }
            },
            modifier = Modifier.fillMaxWidth().height(250.dp   )
        )
    }
}