package com.catchapp.catchapp.data.repository

import com.catchapp.catchapp.data.firebase.auth.FirebaseAuthApi
import io.reactivex.Completable
import io.reactivex.Single

class UserRepositoryImpl(
    private val firebaseApi: FirebaseAuthApi
) : UserRepository {

    override fun singleLoginAnonymously(): Single<Result<Unit>> =
        firebaseApi.isUserLoggedIn()
            .flatMapCompletable { loggedIn ->
                if (loggedIn) Completable.complete()
                else firebaseApi.loginAnonymously()
            }
            .andThen(Single.just(Result.success(Unit)))
}