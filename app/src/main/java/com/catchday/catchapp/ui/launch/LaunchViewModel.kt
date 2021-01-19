package com.catchday.catchapp.ui.launch

import android.app.Application
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.OnLifecycleEvent
import com.catchday.catchapp.data.repository.UserRepository
import com.catchday.catchapp.ui.base.viewmodel.BaseRxViewModel
import com.catchday.catchapp.ui.main.MainActivity
import com.orhanobut.logger.Logger
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicLong

class LaunchViewModel(
    application: Application,
    private val userRepository: UserRepository
) : BaseRxViewModel(application) {

    companion object {

        private const val TIME_OUT: Long = 2000
    }

    private val counter = MutableLiveData(AtomicLong())

    private val timer = BehaviorSubject.create<Boolean>()

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    private fun startTimer() {
        timer.distinctUntilChanged()
            .switchMap { timerIsRunning ->
                if (timerIsRunning) Observable.interval(
                    1L,
                    TimeUnit.MILLISECONDS,
                    Schedulers.computation()
                )
                else Observable.never()
            }
            .map {
                counter.value!!.getAndIncrement()
            }
            .takeWhile { it <= TIME_OUT }
            .filter { it == TIME_OUT }
            .flatMapCompletable { Completable.complete() }
            .mergeWith(userRepository.singleLoginAnonymously().ignoreElement())
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                handleTimerSuccess()
            }) {
                handleTimerError(it)
            }.autoClearOnCleared()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun pauseTimer() {
        timer.onNext(false)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun resumeTimer() {
        timer.onNext(true)
    }

    private fun handleTimerSuccess() {
        navigateToMain()
    }

    private fun handleTimerError(error: Throwable) {
        Logger.d(error)
        navigateToMain()
    }

    private fun navigateToMain() {
        navigateViewWithFinish<MainActivity>()
    }
}