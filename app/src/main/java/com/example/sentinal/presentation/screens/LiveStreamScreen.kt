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
    val context = LocalContext.current

    val rtspUrl = remember(camera) {
        // Remove any existing protocol prefixes if the user added them
        val cleanIp = camera.ipAddress
            .replace("rtsp://", "")
            .replace("http://", "")
            .split("/") // Take only the host/port part if there's a path
            .first()
            .trim()

        val auth = if (camera.username.isNotBlank() && camera.password.isNotBlank()) {
            "${camera.username}:${camera.password}@"
        } else ""

        val portSuffix = if (camera.port != null && camera.port != 0) ":${camera.port}" else ""
        
        // Construct the full RTSP URL
        "rtsp://$auth$cleanIp$portSuffix/h264_ulaw.sdp".also {
            Log.d("LiveStreamScreen", "Constructed RTSP URL: $it")
        }
    }

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

    var surfaceView by remember { mutableStateOf<SurfaceView?>(null) }
    val motionDetector = remember { MotionDetector() }
    var motionDetected by remember { mutableStateOf(false) }

    //Capture loop for motion detection
    LaunchedEffect(surfaceView, isPlaying) {
        while (true) {
            delay(2000)

            val view = surfaceView
            if (view != null && view.holder.surface.isValid && isPlaying) {
                try {
                    val bitmap = Bitmap.createBitmap(
                        view.width, view.height, Bitmap.Config.ARGB_8888
                    )

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
        Button(
            onClick = onNavigationBack, 
            modifier = Modifier.padding(8.dp)
        ) {
            Text("<-Back")
        }

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
                    surfaceView = it.videoSurfaceView as? SurfaceView
                }
            },
            modifier = Modifier.fillMaxWidth().height(250.dp)
        )
    }
}
