package com.catchday.catchapp.ui.base.viewmodel

import android.app.Activity
import android.app.Application
import android.app.Service
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LiveData
import com.catchday.catchapp.ui.base.types.FINISH
import com.catchday.catchapp.ui.base.types.NAVIGATE
import com.catchday.catchapp.ui.common.livedata.SingleLiveEvent
import com.orhanobut.logger.Logger
import com.tedpark.tedonactivityresult.rx2.TedRxOnActivityResult
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.startService

abstract class BaseViewModel(application: Application) : AndroidViewModel(application),
    LifecycleObserver {

    private val _loading =
        SingleLiveEvent<Boolean>()
    val loading: LiveData<Boolean> = _loading

    protected val _navigateEvent =
        SingleLiveEvent<NAVIGATE>()
    val navigateEvent: LiveData<NAVIGATE> = _navigateEvent

    private val _finishEvent =
        SingleLiveEvent<FINISH>()
    val finishEvent: LiveData<FINISH> = _finishEvent

    fun updateLoading(loading: Boolean) {
        _loading.value = loading
    }

    protected inline fun <reified T : Activity> navigateView(vararg params: Pair<String, Any> = emptyArray()) {
        _navigateEvent.postValue {
            startActivity<T>(*params)
        }
    }

    protected inline fun <reified T : Activity> navigateViewForResult(
        requestCode: Int,
        vararg params: Pair<String, Any> = emptyArray(),
        crossinline block: (ActivityResultData) -> Unit
    ) {
        _navigateEvent.postValue {
            val intent = intentFor<T>(*params)
            TedRxOnActivityResult.with(this)
                .startActivityForResult(intent)
                .subscribe({
                    block.invoke(ActivityResultData(requestCode, it.resultCode))
                }) {
                    Logger.d(it)
                }
        }
    }

    data class ActivityResultData(val requestCode: Int, val resultCode: Int)

    protected inline fun <reified T : Activity> navigateViewWithFinish(vararg params: Pair<String, Any> = emptyArray()) {
        _navigateEvent.postValue {
            startActivity<T>(*params)
            onBackPressed()
        }
    }

    protected inline fun <reified T : Service> startService(vararg params: Pair<String, Any> = emptyArray()) {
        _navigateEvent.postValue {
            startService<T>(*params)
        }
    }

    fun finishView() {
        _finishEvent.postValue {
            onBackPressed()
        }
    }
}