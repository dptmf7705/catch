package com.catchday.catchapp.app

import android.app.Application
import com.catchday.catchapp.BuildConfig
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.orhanobut.logger.PrettyFormatStrategy
import io.reactivex.exceptions.UndeliverableException
import io.reactivex.plugins.RxJavaPlugins
import java.io.IOException
import java.net.SocketException

open class App : Application() {

    override fun onCreate() {
        super.onCreate()
        initLogger()
        initRx()
    }

    private fun initLogger() {
        val formatStrategy = PrettyFormatStrategy.newBuilder()
            .showThreadInfo(true)
            .methodCount(3)
            .methodOffset(0)
            .build()

        Logger.addLogAdapter(object : AndroidLogAdapter(formatStrategy) {
            override fun isLoggable(priority: Int, tag: String?): Boolean {
                return BuildConfig.DEBUG
            }
        })
    }

    private fun initRx() {
        RxJavaPlugins.setErrorHandler { e ->
            var error = e
            if (error is UndeliverableException) {
                error = e.cause
            }
            if (error is IOException || error is SocketException) {
                // fine, irrelevant network problem or API that throws on cancellation
                return@setErrorHandler
            }
            if (error is InterruptedException) {
                // fine, some blocking code was interrupted by a dispose call
                return@setErrorHandler
            }
            if (error is NullPointerException || error is IllegalArgumentException) {
                // that's likely a bug in the application
                Thread.currentThread().uncaughtExceptionHandler!!
                    .uncaughtException(Thread.currentThread(), error)
                return@setErrorHandler
            }
            if (error is IllegalStateException) {
                // that's a bug in RxJava or in a custom operator
                Thread.currentThread().uncaughtExceptionHandler!!
                    .uncaughtException(Thread.currentThread(), error)
                return@setErrorHandler
            }
        }
    }
}