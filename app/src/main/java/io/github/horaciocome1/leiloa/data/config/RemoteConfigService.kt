package io.github.horaciocome1.leiloa.data.config

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

class RemoteConfigService : RemoteConfigInterface {

    private val remoteConfig: FirebaseRemoteConfig by lazy {
        FirebaseRemoteConfig.getInstance()
    }

    private val coroutineScope: CoroutineScope by lazy {
        CoroutineScope(Dispatchers.IO)
    }

    override fun loadCompanyDomainLengthAsync(): Flow<Long> =
        coroutineScope

    override fun loadProductIdAsync(): Flow<Long> {
        TODO("Not yet implemented")
    }

    override fun loadTermsAndConditionsLengthAsync(): Flow<Long> {
        TODO("Not yet implemented")
    }

    override fun loadPriceLengthAsync(): Flow<Long> {
        TODO("Not yet implemented")
    }

    override fun loadStartActiveAsync(): Flow<Boolean> {
        TODO("Not yet implemented")
    }

}