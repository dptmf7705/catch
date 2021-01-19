package com.catchday.catchapp.ui.base.views

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.Observer
import com.catchday.catchapp.extension.lifecycle.plusAssign
import com.catchday.catchapp.ui.base.types.FINISH
import com.catchday.catchapp.ui.base.viewmodel.BaseViewModel

abstract class BaseActivity<B : ViewDataBinding>(
    private val lightStatusBar: Boolean = true
) : AppCompatActivity() {

    protected lateinit var binding: B
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBinding()
        initViewModels(savedInstanceState)
        initViews()
    }

    private fun initBinding() {
        binding = DataBindingUtil.setContentView<B>(this, layoutId)
            .apply { lifecycleOwner = this@BaseActivity } as B
    }

    override fun onResume() {
        super.onResume()
        if (lightStatusBar && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { // 상태바 글씨 검정
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
    }

    protected abstract val layoutId: Int

    protected open fun initViewModels(savedInstanceState: Bundle?) {
        // do nothing
    }

    protected open fun initViews() {
        // do nothing
    }

    operator fun <T : BaseViewModel> plusAssign(viewModel: T) {
        viewModel.bindToLifecycle()
        viewModel.navigateEvent.observe(this) { it.invoke(this) }
        viewModel.finishEvent.observeForever(object : Observer<FINISH> {
            override fun onChanged(finish: FINISH?) {
                viewModel.finishEvent.removeObserver(this)
                finish?.invoke(this@BaseActivity)
            }
        })
    }

    protected fun LifecycleObserver.bindToLifecycle() {
        lifecycle += this
    }
}