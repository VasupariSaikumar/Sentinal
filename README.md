# Sentinel — Android CCTV Monitoring App

A real-time CCTV monitoring Android app supporting multiple IP cameras 
via RTSP streaming, with on-device motion detection and instant push notifications.

## Features
- Live RTSP stream playback using ExoPlayer
- Frame-differencing motion detection (no cloud, fully on-device)
- Push notifications on motion events via FCM
- Camera history stored locally using Room database
- Multi-camera support with Jetpack Compose UI
- Resolved MIUI GPU bitmap capture bug for motion detection on Xiaomi devices

## Tech Stack
Kotlin · Jetpack Compose · ExoPlayer · Room · FCM · MVVM · Coroutines · StateFlow

## Architecture
MVVM — ViewModel → StateFlow → Composable UI
Data layer: Room (local) + FCM (push)
Stream layer: ExoPlayer with RTSP URI input

## Screenshots
[Add 2-3 screenshots here]

## What I learned building this
- How to extract frames from a hardware-decoded video stream
- Why MIUI handles GPU bitmaps differently and how to fix it
- Designing a motion sensitivity threshold that avoids false positives
