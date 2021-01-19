package com.catchapp.catchapp.di.ui

import com.catchapp.catchapp.ui.camera.CameraViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

object CameraUiModule {

    val module = module {

        viewModel {
            CameraViewModel(get())
        }
    }
}