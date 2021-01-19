package com.catchday.catchapp.ui.launch

import android.os.Bundle
import android.view.View
import com.catchday.catchapp.R
import com.catchday.catchapp.databinding.ActivityLaunchBinding
import com.catchday.catchapp.ui.base.views.BaseActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class LaunchActivity : BaseActivity<ActivityLaunchBinding>() {

    override val layoutId: Int = R.layout.activity_launch

    private val viewModel: LaunchViewModel by viewModel()

    override fun initViewModels(savedInstanceState: Bundle?) {
        this += viewModel
    }

    override fun onResume() {
        super.onResume()
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
    }
}