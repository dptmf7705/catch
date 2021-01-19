package com.catchday.catchapp.extension.views

import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.animation.OvershootInterpolator
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.LifecycleOwner
import com.catchday.catchapp.extension.lifecycle.plusAssign
import com.catchday.catchapp.ui.common.rx.AutoActivatedDisposable
import com.google.android.material.snackbar.Snackbar
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit

fun View.clicksThrottleFirst(duration: Long = 500, action: () -> Unit): Disposable =
    clicks().throttleFirst(duration, TimeUnit.MILLISECONDS).subscribe { action.invoke() }

fun View.changeVisibility(visible: Boolean?) {
    visible?.let { visibility = if (it) View.VISIBLE else View.GONE }
}

fun View.autoActivatedClicks(
    lifecycleOwner: LifecycleOwner,
    duration: Long = 500,
    action: () -> Unit
) {
    lifecycleOwner.lifecycle += AutoActivatedDisposable(lifecycleOwner) {
        clicksThrottleFirst(duration, action)
    }
}

fun View.showSnackBar(message: String?) {
    message?.let { Snackbar.make(this, it, Snackbar.LENGTH_SHORT).show() }
}

fun View.runAfterGlobalLayout(block: View.() -> Unit) {
    if (viewTreeObserver.isAlive) {
        viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                if (viewTreeObserver.isAlive) {
                    viewTreeObserver.removeOnGlobalLayoutListener(this)
                    block.invoke(this@runAfterGlobalLayout)
                }
            }
        })
    }
}

fun View.runAfterPreDraw(block: View.() -> Unit) {
    if (viewTreeObserver.isAlive) {
        viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                if (viewTreeObserver.isAlive) {
                    viewTreeObserver.removeOnPreDrawListener(this)
                    block.invoke(this@runAfterPreDraw)
                    return true
                }
                return false
            }
        })
    }
}

fun View.startFocusAnimation(block: () -> Unit) {
    animate()?.apply {
        duration = 500L
        scaleX(1.05f)
        scaleY(1.05f)
        interpolator = OvershootInterpolator(2f)
        withEndAction {
            scaleX(1f)
            scaleY(1f)
            block.invoke()
        }
    }?.start()
}

fun View.moveTo(centerX: Float, centerY: Float) {
    updateLayoutParams {
        this as ViewGroup.MarginLayoutParams
        leftMargin = (centerX - (width / 2f)).toInt()
        topMargin = (centerY - (height / 2f)).toInt()
    }
}
