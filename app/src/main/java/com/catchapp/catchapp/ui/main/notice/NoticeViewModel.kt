package com.catchapp.catchapp.ui.main.notice

import android.app.Application
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.OnLifecycleEvent
import com.catchapp.catchapp.R
import com.catchapp.catchapp.ui.base.viewmodel.BaseViewModel

class NoticeViewModel(
    application: Application
) : BaseViewModel(application) {

    private val _noticeList = MutableLiveData<List<Notice>>()
    val noticeList: LiveData<List<Notice>> = _noticeList

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    private fun loadNoticeList() {
        this._noticeList.value = listOf(
            Notice(R.drawable.img_main),
            Notice(R.drawable.img_main),
            Notice(R.drawable.img_main),
            Notice(R.drawable.img_main)
        )
    }
}