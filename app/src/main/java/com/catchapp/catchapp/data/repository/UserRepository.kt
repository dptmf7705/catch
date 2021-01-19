package com.catchapp.catchapp.data.repository

import io.reactivex.Single

interface UserRepository {

    fun singleLoginAnonymously(): Single<Result<Unit>>
}