package com.catchapp.catchapp.extension.livedata

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

fun <T> MutableLiveData<T>.notify() {
    value = value
}

fun <T> LiveData<T>.isNotEmpty() = isEmpty().not()

fun <T> LiveData<T>.isEmpty() = null == value