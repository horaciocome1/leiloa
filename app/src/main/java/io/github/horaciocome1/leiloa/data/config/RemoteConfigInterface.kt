package io.github.horaciocome1.leiloa.data.config

import kotlinx.coroutines.flow.Flow

interface RemoteConfigInterface {

    /**
     * tells the text input length to the company domain field
     */
    fun loadCompanyDomainLengthAsync(): Flow<Long>

    /**
     * tells the text input length to the product id field
     */
    fun loadProductIdAsync(): Flow<Long>

    /**
     * tells the text input length to the terms and conditions field
     */
    fun loadTermsAndConditionsLengthAsync(): Flow<Long>

    /**
     * tells the text input length to the price field
     */
    fun loadPriceLengthAsync(): Flow<Long>

    /**
     * tells if the auction should start activated by default
     */
    fun loadStartActiveAsync(): Flow<Boolean>

}