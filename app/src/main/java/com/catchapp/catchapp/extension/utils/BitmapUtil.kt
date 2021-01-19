package com.catchapp.catchapp.extension.utils

import android.content.Context
import android.graphics.Bitmap
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import java.io.File


fun Context.saveBitmapToTempFile(bitmap: Bitmap): String? =
    File(cacheDir, "$currentDateTimeString.jpg")
        .takeIf { it.createNewFile() }?.let { file ->
            // use : Java 의 try-catch-resource 와 동일
            file.outputStream().use { outStream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream)
            }
            file.toString()
        }