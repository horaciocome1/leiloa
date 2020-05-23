package io.github.horaciocome1.leiloa.data.participants

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow

object ParticipantsRepository: ParticipantsServiceInterface {

    private val service: ParticipantsService = ParticipantsService()

    @ExperimentalCoroutinesApi
    override fun watchParticipants(
        companyDomain: String,
        productID: String
    ): Flow<List<Participant>> =
        service.watchParticipants(companyDomain, productID)

    override fun setPriceAsync(
        companyDomain: String,
        productID: String,
        offer: Int
    ): Deferred<Boolean> =
        service.setPriceAsync(companyDomain, productID, offer)

}