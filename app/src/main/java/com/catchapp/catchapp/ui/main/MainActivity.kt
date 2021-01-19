package com.catchapp.catchapp.ui.main

import android.os.Bundle
import com.catchapp.catchapp.R
import com.catchapp.catchapp.databinding.ActivityMainBinding
import com.catchapp.catchapp.extension.views.autoActivatedClicks
import com.catchapp.catchapp.ui.base.views.BaseActivity
import com.catchapp.catchapp.ui.main.notice.NoticeAdapter
import com.catchapp.catchapp.ui.main.notice.NoticeViewModel
import com.tedpark.tedpermission.rx2.TedRx2Permission
import gun0912.tedimagepicker.builder.TedRxImagePicker
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class MainActivity : BaseActivity<ActivityMainBinding>() {

    override val layoutId = R.layout.activity_main

    private val permissionBuilder: TedRx2Permission.Builder by inject { parametersOf(this) }

    private val imagePickerBuilder: TedRxImagePicker.Builder by inject { parametersOf(this) }

    private val viewModel: MainViewModel by viewModel()

    private val noticeViewModel: NoticeViewModel by viewModel()


    override fun initViewModels(savedInstanceState: Bundle?) {
        this += viewModel
        binding.mainVm = viewModel
        viewModel.initialize(imagePickerBuilder, permissionBuilder)

        binding.noticeVm = noticeViewModel
        this += noticeViewModel
    }

    override fun initViews() {
        window.sharedElementsUseOverlay = false
        window.sharedElementExitTransition = null
        window.sharedElementReturnTransition = null

        binding.rvFeed.adapter = NoticeAdapter()

        binding.tvCamera.autoActivatedClicks(this) {
            viewModel.handleCameraButtonClick()
        }

        binding.tvAlbum.autoActivatedClicks(this) {
            viewModel.handleAlbumButtonClick()
        }
    }
}