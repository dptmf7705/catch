package com.catchapp.catchapp.ui.camera

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.view.children
import com.catchapp.catchapp.R
import com.catchapp.catchapp.databinding.ActivityCameraBinding
import com.catchapp.catchapp.extension.utils.createNewPhotoFile
import com.catchapp.catchapp.extension.views.*
import com.catchapp.catchapp.ui.base.views.BaseActivity
import com.catchapp.catchapp.ui.main.MainViewModel
import com.orhanobut.logger.Logger
import com.tedpark.tedpermission.rx2.TedRx2Permission
import gun0912.tedimagepicker.builder.TedRxImagePicker
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraActivity : BaseActivity<ActivityCameraBinding>() {

    companion object {

        /** Milliseconds used for UI animations */
        const val ANIMATION_FAST_MILLIS = 50L

        const val ANIMATION_SLOW_MILLIS = 100L

        const val FLAGS_FULLSCREEN =
            View.SYSTEM_UI_FLAG_LOW_PROFILE or
                    View.SYSTEM_UI_FLAG_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
    }


    override val layoutId = R.layout.activity_camera

    private val permissionBuilder: TedRx2Permission.Builder by inject { parametersOf(this) }

    private val imagePickerBuilder: TedRxImagePicker.Builder by inject { parametersOf(this) }

    private val mainViewModel: MainViewModel by viewModel()

    private val viewModel: CameraViewModel by viewModel()

    private lateinit var cameraExecutor: ExecutorService

    private var preview: Preview? = null

    private var imageCapture: ImageCapture? = null

    private var cameraProvider: ProcessCameraProvider? = null

    private val focusButton by lazy {
        AppCompatImageView(this).apply {
            val size = resources.getDimensionPixelOffset(R.dimen.button_size_48dp)
            layoutParams = ViewGroup.MarginLayoutParams(size, size)
            scaleType = ImageView.ScaleType.CENTER_CROP
            adjustViewBounds = true
            setImageResource(R.drawable.ic_focus)
        }
    }

    override fun initViewModels(savedInstanceState: Bundle?) {
        this += mainViewModel
        this += viewModel

        mainViewModel.initialize(imagePickerBuilder, permissionBuilder)

        viewModel.cameraConfig.observe(this) { (lensFacing, aspectRatio) ->
            bindCameraUseCase(lensFacing, aspectRatio)
        }
    }

    override fun initViews() {

        binding.ivAlbum.autoActivatedClicks(this) {
            mainViewModel.handleAlbumButtonClick()
        }

        binding.ivCapture.isEnabled = false
        binding.ivCapture.autoActivatedClicks(this) {
            takePhoto()
        }

        binding.ivFlip.isEnabled = false
        binding.ivFlip.autoActivatedClicks(this) {
            viewModel.toggleLensFacing()
        }

        cameraExecutor = Executors.newSingleThreadExecutor()

        startCamera()
    }

    private fun startCamera() {

        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({

            cameraProvider = cameraProviderFuture.get()

            viewModel.initialize(
                when {
                    hasBackCamera() -> CameraLensFacing.LENS_FACING_BACK
                    hasFrontCamera() -> CameraLensFacing.LENS_FACING_FRONT
                    else -> throw IllegalStateException("camera is unavailable")
                },
                CameraAspectRatio.RATIO_4_3
            )

            updateCameraButton()

        }, ContextCompat.getMainExecutor(this))
    }

    private fun updateCameraButton() {

        binding.ivCapture.isEnabled = true

        try {
            binding.ivFlip.isEnabled = hasBackCamera() && hasFrontCamera()
        } catch (exception: CameraInfoUnavailableException) {
            binding.ivFlip.isEnabled = false
        }
    }

    private fun bindCameraUseCase(lensFacing: CameraLensFacing, aspectRatio: CameraAspectRatio) {

        val cameraProvider = cameraProvider
            ?: throw IllegalStateException("Camera initialization failed")

        val cameraSelector = CameraSelector.Builder()
            .requireLensFacing(lensFacing.lensFacing)
            .build()

        preview = Preview.Builder()
            .setTargetAspectRatio(aspectRatio.ratio)
            .build()

        imageCapture = ImageCapture.Builder()
            .setTargetAspectRatio(aspectRatio.ratio)
            .build()

        // unbind old use cases
        cameraProvider.unbindAll()

        try {

            val camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)

            preview?.setSurfaceProvider(binding.viewFinder.createSurfaceProvider())

            updateCameraControlUi(camera, lensFacing == CameraLensFacing.LENS_FACING_BACK)

        } catch (exception: Exception) {
            Logger.d("Use case binding failed $exception")
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun updateCameraControlUi(camera: Camera, enableFocus: Boolean) {

        if (enableFocus.not()) {
            binding.viewFinder.setOnTouchListener(null)
            return
        }

        binding.viewFinder.setOnTouchListener { _, event ->

            when (event.action) {
                MotionEvent.ACTION_DOWN -> return@setOnTouchListener true
                MotionEvent.ACTION_UP -> {

                    // previous focusing event is still on progress
                    if (null != binding.viewFinder.children.find { it == focusButton }) {
                        return@setOnTouchListener true
                    }

                    val focusPoint = SurfaceOrientedMeteringPointFactory(
                        binding.viewFinder.width.toFloat(),
                        binding.viewFinder.height.toFloat()
                    ).createPoint(event.x, event.y)

                    // show focus button with animation
                    binding.viewFinder.addView(focusButton, 1)
                    focusButton.moveTo(event.x, event.y)
                    focusButton.startFocusAnimation {

                        // focus camera
                        camera.cameraControl.startFocusAndMetering(
                            FocusMeteringAction.Builder(
                                focusPoint,
                                FocusMeteringAction.FLAG_AF
                            ).disableAutoCancel().build()
                        )

                        // zoom camera
                        val zoom = camera.cameraInfo.zoomState.value?.zoomRatio ?: 1f
                        camera.cameraControl.setZoomRatio(zoom).addListener({
                            binding.viewFinder.postDelayed({
                                binding.viewFinder.removeView(focusButton)
                            }, 2000)
                        }, cameraExecutor)
                    }

                    return@setOnTouchListener true
                }
                else -> return@setOnTouchListener false // Unhandled event.
            }
        }

        // auto focus to center
        binding.viewFinder.runAfterGlobalLayout {

            val centerWidth = width.toFloat() / 2
            val centerHeight = height.toFloat() / 2

            // create a point on the center of the view
            val autoFocusPoint = SurfaceOrientedMeteringPointFactory(
                width.toFloat(),
                height.toFloat()
            ).createPoint(centerWidth, centerHeight)

            // focus camera
            camera.cameraControl.startFocusAndMetering(
                FocusMeteringAction.Builder(
                    autoFocusPoint,
                    FocusMeteringAction.FLAG_AWB
                ).disableAutoCancel().build()
            )

            // zoom camera
            camera.cameraInfo.zoomState.value?.zoomRatio
                ?.let { camera.cameraControl.setZoomRatio(it) }
        }
    }

    private fun takePhoto() {

        val imageCapture = imageCapture ?: return

        val lensFacing = viewModel.cameraConfig.value?.first?.lensFacing
            ?: CameraSelector.LENS_FACING_BACK

        val photoFile = createNewPhotoFile()

        val metaData = ImageCapture.Metadata().apply {
            // Mirror image when using the front camera
            isReversedHorizontal = lensFacing == CameraSelector.LENS_FACING_FRONT
        }

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile)
            .setMetadata(metaData)
            .build()

        imageCapture.takePicture(
            outputOptions, cameraExecutor, object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    mainViewModel.navigateToDrawView(photoFile.toString())
                }

                override fun onError(exception: ImageCaptureException) {
                    Logger.d("Photo capture failed ${exception.message}")
                }
            }
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            // Display flash animation to indicate that photo was captured
            binding.viewFinder.postDelayed({
                binding.viewFinder.foreground = ColorDrawable(Color.WHITE)
                binding.viewFinder.postDelayed(
                    { binding.viewFinder.foreground = null }, ANIMATION_FAST_MILLIS
                )
            }, ANIMATION_SLOW_MILLIS)
        }
    }

    private fun hasBackCamera() =
        cameraProvider?.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA)
            ?: false

    private fun hasFrontCamera() =
        cameraProvider?.hasCamera(CameraSelector.DEFAULT_FRONT_CAMERA)
            ?: false

    override fun onResume() {
        super.onResume()
        window.decorView.systemUiVisibility = FLAGS_FULLSCREEN
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}