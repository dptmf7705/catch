package com.catchday.catchapp.ui.draw.inpaint

import android.os.Bundle
import com.catchday.catchapp.R
import com.catchday.catchapp.databinding.ActivityInpaintBinding
import com.catchday.catchapp.extension.views.autoActivatedClicks
import com.catchday.catchapp.extension.views.loadImageFileAdjustViewBounds
import com.catchday.catchapp.extension.views.runAfterPreDraw
import com.catchday.catchapp.ui.base.views.BaseActivity
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class InpaintActivity : BaseActivity<ActivityInpaintBinding>(lightStatusBar = false) {

    companion object {

        const val EXTRA_ORIGIN_FILE_PATH = "EXTRA_ORIGIN_FILE_PATH"

        const val EXTRA_INPUT_FILE_PATH = "EXTRA_INPUT_FILE_PATH"
    }

    override val layoutId: Int = R.layout.activity_inpaint

    private val viewModel: InpaintViewModel by viewModel()

    private lateinit var originFilePath: String

    override fun initViewModels(savedInstanceState: Bundle?) {
        this += viewModel
        binding.vm = viewModel

        originFilePath = savedInstanceState?.getString(EXTRA_ORIGIN_FILE_PATH)
            ?: intent.getStringExtra(EXTRA_ORIGIN_FILE_PATH)
                    ?: throw IllegalStateException("originFilePath is required")

        val inputFilePath = savedInstanceState?.getString(EXTRA_INPUT_FILE_PATH)
            ?: intent.getStringExtra(EXTRA_INPUT_FILE_PATH)
            ?: throw IllegalStateException("inputFilePath is required")

        viewModel.initialize(originFilePath, inputFilePath)
    }

    override fun initViews() {
        // start transition animation after image load complete
        postponeEnterTransition()
        binding.cardPhoto.loadImageFileAdjustViewBounds(File(originFilePath)) {
            binding.cardPhoto.runAfterPreDraw {
                postDelayed({ startPostponedEnterTransition() }, 100)
            }
        }

        binding.ivBack.autoActivatedClicks(this) { onBackPressed() }

        binding.tvCatch.autoActivatedClicks(this) { onBackPressed() }
    }

    override fun onBackPressed() {
        finish()
    }
}