package io.github.horaciocome1.leiloa.data.participants

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow

interface ParticipantsServiceInterface {

    /**
     * returns the top 5 participants sorted by price as a flow
     * returns as a flow of deferred
     */
    @ExperimentalCoroutinesApi
    fun watchParticipants(
        companyDomain: String,
        productID: String
    ): Flow<List<Participant>>

    /**
     * allows logged participant to increase product price in multiples of 100, 500 or 1000
     */
    fun setPriceAsync(
        companyDomain: String,
        productID: String,
        price: Int
    ): Deferred<Boolean>

}