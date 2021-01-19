package com.catchday.catchapp.ui.main

import android.app.Application
import com.catchday.catchapp.ui.base.viewmodel.BaseRxViewModel
import com.catchday.catchapp.ui.camera.CameraActivity
import com.catchday.catchapp.ui.draw.DrawActivity
import com.orhanobut.logger.Logger
import com.tedpark.tedpermission.rx2.TedRx2Permission
import gun0912.tedimagepicker.builder.TedRxImagePicker

class MainViewModel(application: Application) : BaseRxViewModel(application) {

    companion object {

        const val REQUEST_CODE_ALBUM = 101

        const val REQUEST_CODE_NULL = 0
    }

    private lateinit var imagePickerBuilder: TedRxImagePicker.Builder

    private lateinit var permissionBuilder: TedRx2Permission.Builder


    internal fun initialize(
        imagePicker: TedRxImagePicker.Builder,
        permission: TedRx2Permission.Builder
    ) {
        this.imagePickerBuilder = imagePicker
        this.permissionBuilder = permission
    }

    fun handleAlbumButtonClick() {
        imagePickerBuilder.start()
            .subscribe({ uri ->
                uri.path?.let { filePath ->
                    navigateToDrawView(filePath, true)
                } ?: handleErrorReturned(
                    IllegalArgumentException("selected file's path has null value")
                )
            }) {
                handleErrorReturned(it)
            }.autoClearOnCleared()
    }

    fun handleCameraButtonClick() {
        permissionBuilder.request()
            .subscribe({ result ->
                if (result.isGranted) {
                    handleRequestPermissionSuccess()
                } else {
                    handleRequestPermissionDenied()
                }
            }) {
                handleErrorReturned(it)
            }.autoClearOnCleared()
    }

    fun navigateToDrawView(filePath: String, openAlbumOnResult: Boolean = false) {
        navigateViewForResult<DrawActivity>(
            if (openAlbumOnResult) REQUEST_CODE_ALBUM else REQUEST_CODE_NULL,
            DrawActivity.EXTRA_FILE_PATH to filePath
        ) { result ->
            if (result.requestCode == REQUEST_CODE_ALBUM) {
                handleAlbumButtonClick()
            }
        }
    }

    private fun handleRequestPermissionSuccess() {
        navigateView<CameraActivity>()
    }

    private fun handleRequestPermissionDenied() {
        // TODO. 권한 요청 메시지
    }

    private fun handleErrorReturned(error: Throwable) {
        Logger.d(error)
    }
}