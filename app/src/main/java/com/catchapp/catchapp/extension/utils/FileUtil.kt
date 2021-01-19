package com.catchapp.catchapp.extension.utils

import android.content.Context
import com.catchapp.catchapp.R
import java.io.File

fun Context.createNewPhotoFile(): File {

    val mediaDir = externalMediaDirs.firstOrNull()?.let {
        File(it, resources.getString(R.string.app_name_eng)).apply { mkdirs() }
    }

    return File(
        if (null != mediaDir && mediaDir.exists()) mediaDir else filesDir,
        "$currentDateTimeString.jpg"
    )
}