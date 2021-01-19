package com.catchapp.catchapp.di.ui

import android.Manifest
import android.content.Context
import com.catchapp.catchapp.R
import com.skydoves.balloon.ArrowConstraints
import com.skydoves.balloon.Balloon
import com.skydoves.balloon.BalloonAnimation
import com.skydoves.balloon.BalloonSizeSpec
import com.tedpark.tedpermission.rx2.TedRx2Permission
import gun0912.tedimagepicker.builder.TedRxImagePicker
import gun0912.tedimagepicker.builder.type.MediaType
import org.koin.dsl.module

object UiLibraryModule {

    val module = module {

        factory { (context: Context) ->
            TedRxImagePicker.with(context)
                .mediaType(MediaType.IMAGE)
                .showCameraTile(false)
                .showTitle(false)
                .backButton(R.drawable.ic_arrow_back)
                .buttonBackground(android.R.color.transparent)
                .buttonTextColor(R.color.colorBlue)
                .drawerAlbum()
        }

        factory { (context: Context) ->
            TedRx2Permission.with(context)
                .setPermissions(
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                .setRationaleTitle("필수 권한 요청")
                .setRationaleMessage("사진 촬영을 위해 [카메라] 및 [저장공간] 접근 권한이 필요합니다.")
        }

        factory { (context: Context) ->
            with(context) {
                val space8dp = resources.getDimensionPixelOffset(R.dimen.space_8dp)
                val space12dp = resources.getDimensionPixelOffset(R.dimen.space_12dp)

                Balloon.Builder(this).apply {
                    isVisibleOverlay = true
                    overlayColor = resources.getColor(R.color.colorBlackTrans, null)

                    arrowSize = space8dp
                    arrowConstraints = ArrowConstraints.ALIGN_ANCHOR

                    width = BalloonSizeSpec.WRAP
                    height = BalloonSizeSpec.WRAP

                    setMargin(6)

                    paddingTop = space8dp
                    paddingBottom = space8dp
                    paddingLeft = space12dp
                    paddingRight = space12dp

                    cornerRadius = space8dp.toFloat()
                    alpha = 0.8f
                    elevation = 2f

                    backgroundColor = resources.getColor(R.color.colorSkyBlue, null)
                    balloonAnimation = BalloonAnimation.FADE
                }
            }
        }
    }
}