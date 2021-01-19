package com.catchapp.catchapp.extension.rxjava

import com.catchapp.catchapp.ui.common.rx.AutoClearedDisposable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

operator fun CompositeDisposable.plusAssign(disposable: Disposable) {
    this.add(disposable)
}

operator fun AutoClearedDisposable.plusAssign(disposable: Disposable) {
    this.add(disposable)
}
