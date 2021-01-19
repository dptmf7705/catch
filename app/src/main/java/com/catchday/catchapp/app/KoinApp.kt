package com.catchday.catchapp.app

import com.catchday.catchapp.di.data.FirebaseModule
import com.catchday.catchapp.di.data.RepositoryModule
import com.catchday.catchapp.di.ui.*
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class KoinApp : App() {

    companion object {

        @JvmStatic
        lateinit var INSTANCE: App
    }

    init {
        INSTANCE = this
    }

    override fun onCreate() {
        super.onCreate()
        initKoin()
    }

    private val dataModules = listOf(
        RepositoryModule.module,
        FirebaseModule.module
    )

    private val uiModules = listOf(
        MainUiModule.module,
        CameraUiModule.module,
        UiLibraryModule.module,
        InpaintUiModule.module
    )

    private fun initKoin() {
        startKoin {
            androidContext(this@KoinApp)
            modules(dataModules + uiModules)
        }
    }
}