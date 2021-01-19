package com.catchapp.catchapp.extension.lifecycle

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver

operator fun Lifecycle.plusAssign(observer: LifecycleObserver) = this.addObserver(observer)

operator fun Lifecycle.minusAssign(observer: LifecycleObserver) = this.removeObserver(observer)