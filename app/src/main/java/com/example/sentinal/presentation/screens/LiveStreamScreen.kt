package com.example.sentinal.presentation.screens

import android.graphics.Bitmap
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.PixelCopy
import android.view.SurfaceView
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
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.rtsp.RtspMediaSource
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
  //  val rtspUrl = "rtsp://${camera.username}:${camera.password}"+"@${camera.ipAddress}:${camera.port}/stream1"
//  To Test on real camera
    val rtspUrl = "rtsp://${camera.ipAddress}:${camera.port}/h264_ulaw.sdp"
        // To test on mobile camera
    var isPlaying by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val exoPlayer = remember(rtspUrl) {
        ExoPlayer.Builder(context).build().apply {
            val mediaSource: MediaSource = RtspMediaSource.Factory()
                .setForceUseRtpTcp(true) // Often more stable on local networks
                .setTimeoutMs(8000) // 8 seconds timeout for connection
                .createMediaSource(MediaItem.fromUri(rtspUrl))
            
            setMediaSource(mediaSource)
            prepare()
            playWhenReady = true
            addListener(object : Player.Listener {
                override fun onIsPlayingChanged(playing: Boolean) {
                    isPlaying = playing
                }

                override fun onPlayerError(error: PlaybackException) {
                    val cause = error.cause?.localizedMessage ?: "Unknown cause"
                    errorMessage = "Playback Error: ${error.localizedMessage} (Cause: $cause)"
                    Log.e("ExoPlayer", "Playback error details", error)
                }
            })
        }
    }
    var surfaceView by remember { mutableStateOf<SurfaceView?>(null) } // Holds reference to the SurfaceView once created
    val motionDetector = remember { MotionDetector() } //Motion detector instance - survives recomposition
    var motionDetected by remember { mutableStateOf(false) } // Track motion state to show on screen

    //Capture loop
    LaunchedEffect(surfaceView, isPlaying) {
        while (true) {
            delay(2000)

            val view = surfaceView
            if (view != null && view.holder.surface.isValid && isPlaying) {
                try {
                    val bitmap = Bitmap.createBitmap(
                        view.width, view.height, Bitmap.Config.ARGB_8888
                    )

                    // PixelCopy needs a Handler to run on
                    val handler = Handler(Looper.getMainLooper())

                    PixelCopy.request(view, bitmap, { result ->
                        if (result == PixelCopy.SUCCESS) {
                            val hasMotion = motionDetector.detectMotion(bitmap)
                            motionDetected = hasMotion

                            if (hasMotion) {
                                Log.d("MotionDetector", "Motion detected!")
                                NotificationHelper.sendMotionAlert(context, camera.name)
                            }
                        } else if (result == PixelCopy.ERROR_SOURCE_NO_DATA) {
                            Log.w("MotionDetector", "PixelCopy: No source data yet (waiting for frame)")
                        } else {
                            Log.w("MotionDetector", "PixelCopy failed: $result")
                        }
                    }, handler)

                } catch (e: Exception) {
                    Log.e("MotionDetector", "Capture error", e)
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
        errorMessage?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(16.dp)
            )
        }
        AndroidView(
            factory = {ctx ->
                val playerView = LayoutInflater.from(ctx)
                    .inflate(R.layout.player_view , null) as PlayerView
                playerView.apply {
                    player = exoPlayer
                    useController = true
                }.also {
                    //capture surface view reference once created
                    surfaceView = it.videoSurfaceView as? SurfaceView
                }
            },
            modifier = Modifier.fillMaxWidth().height(250.dp)
        )
    }
}