package com.catchapp.catchapp.ui.launch

import android.os.Bundle
import android.view.View
import com.catchapp.catchapp.R
import com.catchapp.catchapp.databinding.ActivityLaunchBinding
import com.catchapp.catchapp.ui.base.views.BaseActivity
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