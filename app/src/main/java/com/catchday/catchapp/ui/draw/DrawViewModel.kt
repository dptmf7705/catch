package com.catchday.catchapp.ui.draw

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.catchday.catchapp.ui.base.viewmodel.BaseViewModel

class DrawViewModel(
    application: Application
) : BaseViewModel(application) {

    private val _filePath = MutableLiveData<String>()
    val filePath: LiveData<String> = _filePath

    val paintWidth = MutableLiveData(25)

    private val _paintType = MutableLiveData(DrawView.PaintType.PEN)
    val paintType: LiveData<DrawView.PaintType> = _paintType

    fun initialize(filePath: String) {
        _filePath.value = filePath
    }

    fun handleDrawButtonClick() {
        _paintType.value = DrawView.PaintType.PEN
    }

    fun handleEraseButtonClick() {
        _paintType.value = DrawView.PaintType.ERASER
    }
}