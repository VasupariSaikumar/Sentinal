package com.example.sentinal.utils

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.sentinal.R

object NotificationHelper {
    private const val CHANNEL_ID = "motion_alerts"
    private const val CHANNEL_NAME = "Motion Alerts"

    fun createNotificationChannel(context : Context){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )
            val manager = context.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }
    fun sendMotionAlert(context: Context, cameraName: String){
        val builder = NotificationCompat.Builder(context , CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Motion Detected!")
            .setContentText("Activity spotted on $cameraName")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            if (ActivityCompat.checkSelfPermission(
                context, Manifest.permission.POST_NOTIFICATIONS
            )!= PackageManager.PERMISSION_GRANTED
            ){
                return
            }
        }

        with(NotificationManagerCompat.from(context))  {
            val notificationId = System.currentTimeMillis().toInt()
            notify(notificationId, builder.build())
        }
    }
}