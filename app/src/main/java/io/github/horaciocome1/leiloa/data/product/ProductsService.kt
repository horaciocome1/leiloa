package io.github.horaciocome1.leiloa.data.product

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class ProductsService : ProductsServiceInterface {

    companion object {

        const val COLLECTION_NAME_COMPANIES = "companies"
        const val COLLECTION_NAME_PRODUCTS = "products"

        const val FIELD_NAME_ACTIVE = "active"

        const val MESSAGE_PRODUCT_ID_NOT_FOUND = "Could not find the product ID "
        const val MESSAGE_PRODUCT_ID_NOT_AVAILABLE = "Product ID is not available "

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

    private val crashlytics: FirebaseCrashlytics by lazy {
        val crashlytics = FirebaseCrashlytics.getInstance()
        try {
            crashlytics.setUserId(auth.currentUser!!.uid)
        } catch (exception: FirebaseAuthException) {
            crashlytics.recordException(exception)
        }
        return@lazy crashlytics
    }

    override fun isProductIdRealAsync(
        companyDomain: String,
        productID: String
    ): Deferred<Boolean> =
        coroutineScope.async {
            try {
                val snapshot = companiesCollection.document(companyDomain)
                    .collection(COLLECTION_NAME_PRODUCTS)
                    .document(productID)
                    .get()
                    .await()
                if (snapshot != null && snapshot.exists()) return@async true
                return@async false
            } catch (exception: FirebaseFirestoreException) {
                crashlytics.recordException(exception)
                return@async false
            }
        }

    override fun isProductIdAvailableAsync(
        companyDomain: String,
        productID: String
    ): Deferred<Boolean> =
        coroutineScope.async {
            try {
                val snapshot = companiesCollection.document(companyDomain)
                    .collection(COLLECTION_NAME_PRODUCTS)
                    .document(productID)
                    .get()
                    .await()
                if (snapshot != null && snapshot.exists()) return@async false
                return@async true
            } catch (exception: FirebaseFirestoreException) {
                crashlytics.recordException(exception)
                return@async false
            }
        }

    override fun registerProductAsync(
        companyDomain: String,
        product: Product
    ): Deferred<String> =
        coroutineScope.async {
            try {
                companiesCollection.document(companyDomain)
                    .collection(COLLECTION_NAME_PRODUCTS)
                    .document(product.id)
                    .set(product)
                return@async product.id
            } catch (exception: FirebaseFirestoreException) {
                crashlytics.recordException(exception)
                return@async ""
            }
        }

    override fun setActiveStatusAsync(
        companyDomain: String,
        productID: String,
        isActive: Boolean
    ): Deferred<Boolean> =
        coroutineScope.async {
            try {
                val data = mapOf(
                    FIELD_NAME_ACTIVE to isActive
                )
                companiesCollection.document(companyDomain)
                    .collection(COLLECTION_NAME_PRODUCTS)
                    .document(productID)
                    .set(data, SetOptions.merge())
                return@async true
            } catch (exception: FirebaseFirestoreException) {
                crashlytics.recordException(exception)
                return@async false
            }
        }

    @ExperimentalCoroutinesApi
    override fun watchProduct(
        companyDomain: String,
        productID: String
    ): Flow<Product> =
        callbackFlow {
            val listener = EventListener<DocumentSnapshot> { snapshot, exception ->
                if (exception == null && snapshot != null && snapshot.exists())
                    snapshot.toObject<Product>()?.also {
                        val isActive = snapshot[FIELD_NAME_ACTIVE] as Boolean
                        it.active = isActive
                        offer(it)
                    }
                else if (exception != null)
                    crashlytics.recordException(exception)
            }
            val registration = companiesCollection.document(companyDomain)
                .collection(COLLECTION_NAME_PRODUCTS)
                .document(productID)
                .addSnapshotListener(listener)
            awaitClose { registration.remove() }
        }

}