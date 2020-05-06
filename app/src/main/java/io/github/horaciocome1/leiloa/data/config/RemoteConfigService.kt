package io.github.horaciocome1.leiloa.data.config

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfigException
import io.github.horaciocome1.leiloa.util.myCrashlytics
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.CoroutineContext

class RemoteConfigService(
    override val coroutineContext: CoroutineContext = Dispatchers.IO
) : RemoteConfigInterface, CoroutineScope {

    private val remoteConfig: FirebaseRemoteConfig by lazy {
        FirebaseRemoteConfig.getInstance().apply {
            setDefaultsAsync(RemoteConfigDefaults.DEFAULTS)
        }

    }

    val crashlytics: FirebaseCrashlytics by lazy {
        Firebase.myCrashlytics
    }

    override fun retrieveCompanyDomainMaxLengthAsync(): Deferred<Long> =
        async {
            try {
                remoteConfig.fetchAndActivate()
                    .await()
                return@async remoteConfig.getLong(
                    RemoteConfigDefaults.MAX_LENGTH_COMPANY_DOMAIN
                )
            } catch (exception: FirebaseRemoteConfigException) {
                crashlytics.recordException(exception)
                return@async 0L
            }
        }

    override fun retrieveProductIdMaxLengthAsync(): Deferred<Long> =
        async {
            try {
                remoteConfig.fetchAndActivate()
                    .await()
                return@async remoteConfig.getLong(
                    RemoteConfigDefaults.MAX_LENGTH_PRODUCT_ID
                )
            } catch (exception: FirebaseRemoteConfigException) {
                crashlytics.recordException(exception)
                return@async 0L
            }
        }

    override fun retrieveTermsAndConditionsMaxLengthAsync(): Deferred<Long> =
        async {
            try {
                remoteConfig.fetchAndActivate()
                    .await()
                return@async remoteConfig.getLong(
                    RemoteConfigDefaults.MAX_LENGTH_TERMS_AND_CONDITIONS
                )
            } catch (exception: FirebaseRemoteConfigException) {
                crashlytics.recordException(exception)
                return@async 0L
            }
        }

    override fun retrieveStartPriceMaxLengthAsync(): Deferred<Long> =
        async {
            try {
                remoteConfig.fetchAndActivate()
                    .await()
                return@async remoteConfig.getLong(RemoteConfigDefaults.MAX_LENGTH_START_PRICE)
            } catch (exception: FirebaseRemoteConfigException) {
                crashlytics.recordException(exception)
                return@async 0L
            }
        }

    override fun retrieveStartActiveAsync(): Deferred<Boolean> =
        async {
            try {
                remoteConfig.fetchAndActivate()
                    .await()
                return@async remoteConfig.getBoolean(
                    RemoteConfigDefaults.START_ACTIVE_BY_DEFAULT
                )
            } catch (exception: FirebaseRemoteConfigException) {
                crashlytics.recordException(exception)
                return@async false
            }
        }
}