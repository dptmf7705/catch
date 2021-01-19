package com.catchapp.catchapp.ui.common.rx

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.catchapp.catchapp.extension.lifecycle.minusAssign
import com.catchapp.catchapp.extension.rxjava.plusAssign
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

class AutoClearedDisposable(
    private val lifecycleOwner: LifecycleOwner,
    private val alwaysClearOnStop: Boolean = true,
    private val disposables: CompositeDisposable = CompositeDisposable()
) : LifecycleObserver {

    // lifecycleOwner 의 현재 상태가 INITIALIZED 이후의 상태인지 확인
    // 참이 아닌 경우 IllegalStateException 발생
    // 참인 경우 disposable 추가
    fun add(disposable: Disposable) {
        check(lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.INITIALIZED))
        disposables += disposable
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun cleanUp() {
        // onStop 일 때 무조건 해제하지 않는 경우
        // 액티비티가 종료되지 않는 시점(다른 액티비티 호출 혹은 화면 꺼짐)에는 디스포저블 해제하지 않도록
        when (lifecycleOwner) {
            is FragmentActivity -> {
                if (alwaysClearOnStop.not() && lifecycleOwner.isFinishing.not()) {
                    return
                }
            }
            is Fragment -> {
                if (alwaysClearOnStop.not() && lifecycleOwner.isDetached.not()) {
                    return
                }
            }
        }
        disposables.clear()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun detachSelf() {
        disposables.clear()
        // 라이프사이클 구독 해제
        lifecycleOwner.lifecycle -= this
    }
}