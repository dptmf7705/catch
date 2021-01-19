package com.catchapp.catchapp.ui.camera

import androidx.camera.core.CameraSelector

enum class CameraLensFacing(val lensFacing: Int) {

    LENS_FACING_FRONT(CameraSelector.LENS_FACING_FRONT),

    LENS_FACING_BACK(CameraSelector.LENS_FACING_BACK)
}