package com.catchday.catchapp.ui.common.rx

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.catchday.catchapp.extension.lifecycle.minusAssign
import io.reactivex.disposables.Disposable

class AutoActivatedDisposable(
    private val lifecycleOwner: LifecycleOwner,
    // 이벤트를 받은 디스포저블 객체를 만드는 함수
    private val func: () -> Disposable
) : LifecycleObserver {

    private var disposable: Disposable? = null

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun activate() {
        // 이벤트 구독 시작
        disposable = func.invoke()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun deactivate() {
        // 구독 해제
        disposable?.dispose()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun detachSelf() {
        // 생명주기 이벤트 구독 해제
        lifecycleOwner.lifecycle -= this
    }
}