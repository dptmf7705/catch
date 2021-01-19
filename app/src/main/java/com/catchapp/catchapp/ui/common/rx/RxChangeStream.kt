package com.catchapp.catchapp.ui.common.rx

import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

class RxChangeStream<T: Any> {

    private val _events = BehaviorSubject.create<T>()
    val events: Observable<T> = _events

    fun onNext(event: T) {
        _events.onNext(event)
    }

    fun hasValue() = _events.hasValue()
}