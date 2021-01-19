package com.catchapp.catchapp.ui.camera

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.catchapp.catchapp.ui.base.viewmodel.BaseViewModel

class CameraViewModel(
    application: Application
) : BaseViewModel(application) {

    private val _cameraConfig = MutableLiveData<Pair<CameraLensFacing, CameraAspectRatio>>()
    val cameraConfig: LiveData<Pair<CameraLensFacing, CameraAspectRatio>> = _cameraConfig

    fun initialize(lensFacing: CameraLensFacing, aspectRatio: CameraAspectRatio) {
        _cameraConfig.value = lensFacing to aspectRatio
    }

    fun toggleLensFacing() {
        _cameraConfig.value = _cameraConfig.value?.let { (lensFacing, aspectRatio) ->
            if (lensFacing == CameraLensFacing.LENS_FACING_BACK) {
                CameraLensFacing.LENS_FACING_FRONT to aspectRatio
            } else {
                CameraLensFacing.LENS_FACING_BACK to aspectRatio
            }
        }
    }

    fun toggleAspectRatio() {
        _cameraConfig.value = _cameraConfig.value?.let { (lensFacing, aspectRatio) ->
            if (aspectRatio == CameraAspectRatio.RATIO_4_3) {
                lensFacing to CameraAspectRatio.RATIO_16_9
            } else {
                lensFacing to CameraAspectRatio.RATIO_4_3
            }
        }
    }
}