package com.catchday.catchapp.ui.base.viewmodel

import android.app.Application
import com.catchday.catchapp.extension.rxjava.plusAssign
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

abstract class BaseRxViewModel(application: Application) : BaseViewModel(application) {

    private val compositeDisposable = CompositeDisposable()

    protected fun Disposable.autoClearOnCleared() {
        compositeDisposable += this
    }

    override fun onCleared() {
        compositeDisposable.clear()
        super.onCleared()
    }

}