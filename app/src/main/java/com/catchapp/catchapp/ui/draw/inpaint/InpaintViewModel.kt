package com.catchapp.catchapp.ui.draw.inpaint

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.catchapp.catchapp.ui.base.viewmodel.BaseViewModel

class InpaintViewModel(
    application: Application
) : BaseViewModel(application) {

    private val _inputFilePath = MutableLiveData<String>()

    private val _filePath = MutableLiveData<String>()
    val filePath: LiveData<String> = _filePath

    fun initialize(originFilePath: String, inputFilePath: String) {
        _filePath.value = originFilePath
        _inputFilePath.value = inputFilePath
    }
}