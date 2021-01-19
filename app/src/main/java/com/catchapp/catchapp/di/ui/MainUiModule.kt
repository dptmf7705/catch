package com.catchapp.catchapp.di.ui

import com.catchapp.catchapp.ui.launch.LaunchViewModel
import com.catchapp.catchapp.ui.main.MainViewModel
import com.catchapp.catchapp.ui.main.notice.NoticeViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

object MainUiModule {

    val module = module {

        viewModel {
            MainViewModel(get())
        }

        viewModel {
            NoticeViewModel(get())
        }

        viewModel {
            LaunchViewModel(get(), get())
        }
    }
}