package com.catchday.catchapp.di.ui

import com.catchday.catchapp.ui.draw.DrawViewModel
import com.catchday.catchapp.ui.draw.inpaint.InpaintViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

object InpaintUiModule {

    val module = module {

        viewModel {
            DrawViewModel(get())
        }

        viewModel {
            InpaintViewModel(get())
        }
    }
}