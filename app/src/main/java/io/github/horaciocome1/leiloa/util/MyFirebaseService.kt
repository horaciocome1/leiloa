package io.github.horaciocome1.leiloa.util

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

open class MyFirebaseService(
    override val coroutineContext: CoroutineContext = Dispatchers.IO
) : CoroutineScope {

    val firestore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    val auth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    val crashlytics: FirebaseCrashlytics by lazy {
        val crashlytics = FirebaseCrashlytics.getInstance()
        try {
            crashlytics.setUserId(auth.currentUser!!.uid)
        } catch (exception: FirebaseAuthException) {
            crashlytics.recordException(exception)
        }
        return@lazy crashlytics
    }

}