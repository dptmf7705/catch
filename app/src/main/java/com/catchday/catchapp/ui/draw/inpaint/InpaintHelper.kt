package com.catchday.catchapp.ui.draw.inpaint

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect

object InpaintHelper {

    private const val IMAGE_WIDTH = 680

    private const val IMAGE_HEIGHT = 512

    fun createInputImage(image: Bitmap, mask: Bitmap): Bitmap? =
        Bitmap.createBitmap(IMAGE_WIDTH * 2, IMAGE_HEIGHT, Bitmap.Config.ARGB_8888)
            .also { inputImage ->

                val originRect = Rect(0, 0, image.width, image.height)

                val imageAspectRect = Rect(0, 0, IMAGE_WIDTH, IMAGE_HEIGHT)
                val maskAspectRect = Rect(IMAGE_WIDTH, 0, IMAGE_WIDTH * 2, IMAGE_HEIGHT)

                Canvas(inputImage).run {
                    drawBitmap(image, originRect, imageAspectRect, null)
                    drawBitmap(mask, originRect, maskAspectRect, null)
                }
            }
}