package io.github.horaciocome1.leiloa.data.participants

import com.google.firebase.FirebaseException
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.toObjects
import io.github.horaciocome1.leiloa.util.MyFirebaseService
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class ParticipantsService : ParticipantsServiceInterface, MyFirebaseService() {

    companion object {

        const val COLLECTION_NAME_COMPANIES = "companies"
        const val COLLECTION_NAME_PARTICIPANTS = "participants"
        const val COLLECTION_NAME_PRODUCTS = "products"

        const val FIELD_NAME_OFFER = "offer"

        const val TOTAL_TOP_PARTICIPANTS = 5L

        const val INCREASE_100 = 100
        const val INCREASE_500 = 500
        const val INCREASE_1000 = 1000

    }

    private val companiesCollection: CollectionReference by lazy {
        firestore.collection(COLLECTION_NAME_COMPANIES)
    }

    @ExperimentalCoroutinesApi
    override fun watchParticipants(
        companyDomain: String,
        productID: String
    ): Flow<List<Participant>> = callbackFlow {
        val listener = EventListener<QuerySnapshot> { snapshot, exception ->
            if (exception == null && snapshot != null && !snapshot.isEmpty)
                snapshot.toObjects<Participant>().also { offer(it) }
            else if (exception != null)
                crashlytics.recordException(exception)
        }
        val registration = companiesCollection.document(companyDomain)
            .collection(COLLECTION_NAME_PRODUCTS)
            .document(productID)
            .collection(COLLECTION_NAME_PARTICIPANTS)
            .orderBy(FIELD_NAME_OFFER, Query.Direction.DESCENDING)
            .limit(TOTAL_TOP_PARTICIPANTS)
            .addSnapshotListener(listener)
        awaitClose { registration.remove() }
    }

    override fun setPriceAsync(
        companyDomain: String,
        productID: String,
        offer: Int
    ): Deferred<Boolean> = async {
        try {
            val uid = auth.currentUser!!.uid
            val name = auth.currentUser!!.displayName!!
            val participant = Participant(id = uid, name = name, offer = offer)
            val product = mapOf(
                FIELD_NAME_OFFER to offer
            )
            val batch = firestore.batch()
            val participantRef = companiesCollection.document(companyDomain)
                .collection(COLLECTION_NAME_PRODUCTS)
                .document(productID)
                .collection(COLLECTION_NAME_PARTICIPANTS)
                .document(uid)
            val productRef = companiesCollection.document(companyDomain)
                .collection(COLLECTION_NAME_PRODUCTS)
                .document(productID)
            batch.set(participantRef, participant, SetOptions.merge())
            batch.set(productRef, product, SetOptions.merge())
            batch.commit()
                .await()
            return@async true
        } catch (exception: FirebaseException) {
            crashlytics.recordException(exception)
            return@async false
        }

    }

}