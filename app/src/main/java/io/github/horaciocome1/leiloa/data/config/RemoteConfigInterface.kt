package io.github.horaciocome1.leiloa.data.config

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow

interface RemoteConfigInterface {

    /**
     * tells the text input length to the company domain field
     */
    fun retrieveCompanyDomainMaxLengthAsync(): Deferred<Long>

    /**
     * tells the text input length to the product id field
     */
    fun retrieveProductIdMaxLengthAsync(): Deferred<Long>

    /**
     * tells the text input length to the terms and conditions field
     */
    fun retrieveTermsAndConditionsMaxLengthAsync(): Deferred<Long>

    /**
     * tells the text input length to the price field
     */
    fun retrieveStartPriceMaxLengthAsync(): Deferred<Long>

    /**
     * tells if the auction should start activated by default
     */
    fun retrieveStartActiveAsync(): Deferred<Boolean>

}