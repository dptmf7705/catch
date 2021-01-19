package com.catchday.catchapp.di.data

import com.catchday.catchapp.data.firebase.auth.FirebaseAuthApi
import com.google.firebase.auth.FirebaseAuth
import org.koin.dsl.module

object FirebaseModule {

    val module = module {

        single {
            FirebaseAuth.getInstance()
        }

        single {
            FirebaseAuthApi(get())
        }
    }
}