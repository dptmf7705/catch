package com.catchday.catchapp.ui.draw

import android.app.ActivityOptions
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.catchday.catchapp.R
import com.catchday.catchapp.databinding.ActivityDrawBinding
import com.catchday.catchapp.databinding.LayoutPenStrokeBinding
import com.catchday.catchapp.databinding.LayoutPenTypeBinding
import com.catchday.catchapp.extension.utils.saveBitmapToTempFile
import com.catchday.catchapp.extension.views.autoActivatedClicks
import com.catchday.catchapp.extension.views.loadImageFileAdjustViewBounds
import com.catchday.catchapp.ui.base.views.BaseActivity
import com.catchday.catchapp.ui.common.rx.AutoActivatedDisposable
import com.catchday.catchapp.ui.draw.inpaint.InpaintActivity
import com.catchday.catchapp.ui.draw.inpaint.InpaintHelper
import com.skydoves.balloon.ArrowOrientation
import com.skydoves.balloon.Balloon
import com.skydoves.balloon.overlay.BalloonOverlayCircle
import com.skydoves.balloon.overlay.BalloonOverlayRoundRect
import io.reactivex.android.schedulers.AndroidSchedulers
import org.jetbrains.anko.intentFor
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.io.File

class DrawActivity : BaseActivity<ActivityDrawBinding>() {

    companion object {

        const val EXTRA_FILE_PATH = "EXTRA_FILE_PATH"
    }

    override val layoutId: Int = R.layout.activity_draw

    private val helpBalloonBuilder: Balloon.Builder by inject { parametersOf(this) }

    private val strokeBalloonBuilder: Balloon.Builder by inject { parametersOf(this) }

    private val penTypeBalloonBuilder: Balloon.Builder by inject { parametersOf(this) }

    private val viewModel: DrawViewModel by viewModel()

    private lateinit var filePath: String

    private lateinit var helpBalloon: Balloon

    private lateinit var strokeBalloon: Balloon

    private lateinit var penTypeBalloon: Balloon


    override fun initViewModels(savedInstanceState: Bundle?) {

        this += viewModel
        binding.vm = viewModel

        filePath = savedInstanceState?.getString(EXTRA_FILE_PATH)
            ?: intent.getStringExtra(EXTRA_FILE_PATH)
                    ?: throw IllegalStateException("filePath is required")

        viewModel.initialize(filePath)

        with(viewModel) {
            paintWidth.observe(this@DrawActivity) {
                binding.drawView.changePaintWidth(it.toFloat())
            }
            paintType.observe(this@DrawActivity) {
                binding.drawView.changePaintType(it)
            }
        }
    }

    override fun initViews() {

        initImage()

        updateDrawingControlUI()

        AutoActivatedDisposable(this) {
            binding.drawView.drawingStackChanges.events
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { updateDrawingControlUI() }
        }.bindToLifecycle()

        initHelpBalloon()
        initStrokeBalloon()
        initPenTypeBalloon()

        observeButtons()
    }

    private fun observeButtons() {

        binding.ivBack.autoActivatedClicks(this) {
            onBackPressed()
        }

        binding.ivHelp.autoActivatedClicks(this) {
            showHelpBalloon()
        }

        binding.ivUndo.autoActivatedClicks(this, 200L) {
            binding.drawView.removeLatestPath()
        }

        binding.ivRedo.autoActivatedClicks(this, 200L) {
            binding.drawView.redrawLatestRemovedPath()
        }

        binding.ivStroke.autoActivatedClicks(this) {
            showStrokeBalloon()
        }

        binding.ivType.autoActivatedClicks(this) {
            showPenTypeBalloon()
        }

        binding.ivDone.autoActivatedClicks(this) {
            if (binding.drawView.isEmptyDrawing()) {
                showHelpBalloon()
            } else {
                handleDoneButtonClick()
            }
        }
    }

    private fun initImage() {

        viewModel.updateLoading(true)

        binding.ivImage.loadImageFileAdjustViewBounds(File(filePath)) {
            viewModel.updateLoading(false)
        }
    }

    private fun updateDrawingControlUI() {

        binding.drawView.isEmptyDrawing().not().let {
            binding.ivUndo.isEnabled = it
            binding.tvUndo.isEnabled = it
        }

        binding.drawView.isEmptyBackStack().not().let {
            binding.ivRedo.isEnabled = it
            binding.tvRedo.isEnabled = it
        }
    }

    private fun initPenTypeBalloon() {

        penTypeBalloon = with(penTypeBalloonBuilder) {
            setLayout(R.layout.layout_pen_type)
            setLifecycleOwner(this@DrawActivity)
            arrowOrientation = ArrowOrientation.BOTTOM
            overlayShape = BalloonOverlayCircle(55f)
            build()
        }

        DataBindingUtil.bind<LayoutPenTypeBinding>(
            penTypeBalloon.getContentView().findViewById(R.id.container)
        )?.run {
            vm = viewModel

            ivDraw.autoActivatedClicks(this@DrawActivity) {
                viewModel.handleDrawButtonClick()
                penTypeBalloon.dismissWithDelay(100)
            }

            ivEraser.autoActivatedClicks(this@DrawActivity) {
                viewModel.handleEraseButtonClick()
                penTypeBalloon.dismissWithDelay(100)
            }
        }
    }

    private fun initStrokeBalloon() {

        strokeBalloon = with(strokeBalloonBuilder) {
            setLayout(R.layout.layout_pen_stroke)
            setLifecycleOwner(this@DrawActivity)
            arrowOrientation = ArrowOrientation.BOTTOM
            overlayShape = BalloonOverlayCircle(55f)
            build()
        }

        DataBindingUtil.bind<LayoutPenStrokeBinding>(
            strokeBalloon.getContentView().findViewById(R.id.container)
        )?.vm = viewModel
    }

    private fun initHelpBalloon() {

        helpBalloon = with(helpBalloonBuilder) {
            setText(resources.getString(R.string.draw_mask_help))
            setLifecycleOwner(this@DrawActivity)
            setAutoDismissDuration(5000L)
            overlayShape = BalloonOverlayRoundRect(20f, 20f)
            arrowOrientation = ArrowOrientation.TOP
            textSize = 14f
            textColor = Color.WHITE
            build()
        }
    }

    private fun showHelpBalloon() {
        helpBalloon.showAlignBottom(binding.ivImage)
    }

    private fun showStrokeBalloon() {
        strokeBalloon.showAlignTop(binding.ivStroke)
    }

    private fun showPenTypeBalloon() {
        penTypeBalloon.showAlignTop(binding.ivType)
    }

    private fun handleDoneButtonClick() {

        updateLoadingUI(true)

        val imageBitmap = BitmapFactory.decodeFile(filePath)
        val maskBitmap = binding.drawView.getDrawBitmap() ?: return

        val inputImageBitmap = InpaintHelper.createInputImage(imageBitmap, maskBitmap) ?: return

        saveBitmapToTempFile(inputImageBitmap)?.let { inputFilePath ->
            navigateToInpaint(inputFilePath)
        }
    }

    private fun updateLoadingUI(loading: Boolean) {

        viewModel.updateLoading(loading)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            binding.container.foreground = if (loading) {
                ColorDrawable(ContextCompat.getColor(this, R.color.colorWhiteDark))
            } else {
                null
            }
        }
    }

    private fun navigateToInpaint(inputFilePath: String) {
        val intent = intentFor<InpaintActivity>(
            InpaintActivity.EXTRA_ORIGIN_FILE_PATH to filePath,
            InpaintActivity.EXTRA_INPUT_FILE_PATH to inputFilePath
        )

        val transition = ActivityOptions.makeSceneTransitionAnimation(
            this,
            binding.ivImage,
            resources.getString(R.string.transition_image)
        ).toBundle()

        supportFinishAfterTransition()
        startActivity(intent, transition)
    }

    override fun onResume() {
        super.onResume()
        window.sharedElementsUseOverlay = false
    }
}