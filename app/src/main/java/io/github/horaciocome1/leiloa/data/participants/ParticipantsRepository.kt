package io.github.horaciocome1.leiloa.data.participants

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow

class ParticipantsRepository private constructor(
    private val service: ParticipantsService = ParticipantsService()
) : ParticipantsServiceInterface {

    companion object {

        @Volatile
        private var instance: ParticipantsRepository? = null

        fun getInstance() = instance ?: synchronized(this) {
            instance ?: ParticipantsRepository().also { instance = it }
        }

    }

    @ExperimentalCoroutinesApi
    override fun watchParticipants(
        companyDomain: String,
        productID: String
    ): Flow<List<Participant>> =
        service.watchParticipants(companyDomain, productID)

    override fun setPriceAsync(
        companyDomain: String,
        productID: String,
        price: Int
    ): Deferred<Boolean> =
        service.setPriceAsync(companyDomain, productID, price)

}