package io.github.horaciocome1.leiloa.data.participants

import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.toObjects
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class ParticipantsService : ParticipantsServiceInterface {

    companion object {

        const val COLLECTION_NAME_COMPANIES = "companies"
        const val COLLECTION_NAME_PARTICIPANTS = "participants"
        const val COLLECTION_NAME_PRODUCTS = "products"

        const val FIELD_NAME_PRICE = "price"

        const val TOTAL_TOP_PARTICIPANTS = 5L

        const val INCREASE_100 = 100
        const val INCREASE_500 = 500
        const val INCREASE_1000 = 1000

    }

    private val firestore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    private val auth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    private val companiesCollection: CollectionReference by lazy {
        firestore.collection(COLLECTION_NAME_COMPANIES)
    }

    private val coroutineScope: CoroutineScope by lazy {
        CoroutineScope(Dispatchers.IO)
    }

    @ExperimentalCoroutinesApi
    override fun watchParticipants(
        companyDomain: String,
        productID: String
    ): Flow<List<Participant>>  =
        callbackFlow {
            val listener = EventListener<QuerySnapshot> { snapshot, exception ->
                if (exception == null && snapshot != null && !snapshot.isEmpty) {
                    snapshot.toObjects<Participant>().also { offer(it) }
                }
            }
            val registration = companiesCollection.document(companyDomain)
                .collection(COLLECTION_NAME_PRODUCTS)
                .document(productID)
                .collection(COLLECTION_NAME_PARTICIPANTS)
                .orderBy(FIELD_NAME_PRICE, Query.Direction.DESCENDING)
                .limit(TOTAL_TOP_PARTICIPANTS)
                .addSnapshotListener(listener)
            awaitClose { registration.remove() }
        }

    override fun setPriceAsync(
        companyDomain: String,
        productID: String,
        price: Int
    ): Deferred<Boolean> =
        coroutineScope.async {
            try {
                val uid = auth.currentUser!!.uid
                val name = auth.currentUser!!.uid
                val participant = Participant(id = uid, name = name, price = price)
                val product = mapOf(
                    FIELD_NAME_PRICE to price
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
            } catch (exception: FirebaseException) { return@async false }

        }

}