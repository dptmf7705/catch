package com.catchapp.catchapp.di.data

import com.catchapp.catchapp.data.repository.*
import org.koin.dsl.module

object RepositoryModule {

    val module = module {

        single<UserRepository> {
            UserRepositoryImpl(get())
        }
    }
}