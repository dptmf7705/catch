package com.catchapp.catchapp.ui.base.viewmodel

import android.app.Activity
import android.app.Application
import com.catchapp.catchapp.extension.rxjava.plusAssign
import com.gun0912.tedonactivityresult.model.ActivityResult
import com.orhanobut.logger.Logger
import com.tedpark.tedonactivityresult.rx2.TedRxOnActivityResult
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import org.jetbrains.anko.intentFor

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