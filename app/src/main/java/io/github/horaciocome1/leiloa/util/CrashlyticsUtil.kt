package io.github.horaciocome1.leiloa.util

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.ktx.Firebase

val Firebase.myCrashlytics: FirebaseCrashlytics by lazy {
    val crashlytics = FirebaseCrashlytics.getInstance()
    try {
        val auth = FirebaseAuth.getInstance()
        crashlytics.setUserId(auth.currentUser!!.uid)
    } catch (exception: FirebaseAuthException) {
        crashlytics.recordException(exception)
    }
    return@lazy crashlytics
}