package io.github.horaciocome1.leiloa.data.config

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.ExperimentalCoroutinesApi

class RemoteConfigRepository private constructor(
    private val service: RemoteConfigService = RemoteConfigService()
) : RemoteConfigInterface {

    companion object {

        private var instance: RemoteConfigRepository? = null

        fun getInstance() = instance ?: synchronized(this) {
            instance ?: RemoteConfigRepository().also {
                instance = it
            }
        }

    }

    override fun retrieveCompanyDomainMaxLengthAsync(): Deferred<Long> =
        service.retrieveCompanyDomainMaxLengthAsync()

    override fun retrieveProductIdMaxLengthAsync(): Deferred<Long> =
        service.retrieveProductIdMaxLengthAsync()

    override fun retrieveTermsAndConditionsMaxLengthAsync(): Deferred<Long> =
        service.retrieveTermsAndConditionsMaxLengthAsync()

    override fun retrieveStartPriceMaxLengthAsync(): Deferred<Long> =
        service.retrieveStartPriceMaxLengthAsync()

    override fun retrieveStartActiveAsync(): Deferred<Boolean> =
        service.retrieveStartActiveAsync()

}