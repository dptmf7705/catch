package com.catchday.catchapp.di.data

import com.catchday.catchapp.data.repository.*
import org.koin.dsl.module

object RepositoryModule {

    val module = module {

        single<UserRepository> {
            UserRepositoryImpl(get())
        }
    }
}