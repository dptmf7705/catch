package com.catchapp.catchapp.data.firebase.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

class FirebaseAuthApi(
    private val firebaseAuth: FirebaseAuth
) {

    fun isUserLoggedIn(): Single<Boolean> =
        Single.create<Boolean> { emitter ->
            firebaseAuth.currentUser?.run {
                emitter.onSuccess(true)
            } ?: emitter.onSuccess(false)
        }.subscribeOn(Schedulers.io())

    fun loginAnonymously(): Completable =
        Completable.create { emitter ->
            firebaseAuth.signInAnonymously()
                .addOnSuccessListener {
                    emitter.onComplete()
                }
                .addOnFailureListener {
                    emitter.onError(it)
                }
        }.subscribeOn(Schedulers.io())
}