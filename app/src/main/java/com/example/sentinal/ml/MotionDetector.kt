package com.example.sentinal.ml

import android.R.attr.y
import android.graphics.Bitmap
import android.graphics.Color
import kotlin.math.abs

class MotionDetector(
    private val threshold : Int = 30, // pixel difference Sensitivity
    private val motionPercentage : Float = 0.02f // 2% of pixels changed = motion
) {
    private var previousBitmap: Bitmap? = null

    fun detectMotion(currentBitmap: Bitmap): Boolean {
        val previous = previousBitmap

        if (previous == null) {
            previousBitmap = currentBitmap
            return false
        }
        var changedPixels = 0
        var totalChecked = 0
        val width = currentBitmap.width
        val height = currentBitmap.height
        for (x in 0 until width step 4) {
            for (y in 0 until height step 4) {
                val currentPixel = currentBitmap.getPixel(x, y)
                val previousPixel = previous.getPixel(x, y)

                val currentGray = toGrayscale(currentPixel)
                val previousGray = toGrayscale(previousPixel)

                val difference = abs(currentGray - previousGray)

                if (difference > threshold) {
                    changedPixels++
                }
                totalChecked++
            }
        }
        previousBitmap = currentBitmap

        val changeRatio = changedPixels.toFloat()/totalChecked
        return  changeRatio > motionPercentage
    }
    private fun toGrayscale(pixel : Int): Int{
        val red = Color.red(pixel)
        val green = Color.green(pixel)
        val blue = Color.blue(pixel)
        return (red + green + blue )/3
    }
}