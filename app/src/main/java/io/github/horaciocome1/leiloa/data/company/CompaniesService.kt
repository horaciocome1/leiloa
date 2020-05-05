package io.github.horaciocome1.leiloa.data.company

import com.google.firebase.FirebaseException
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestoreException
import io.github.horaciocome1.leiloa.util.MyFirebaseService
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.tasks.await

class CompaniesService : CompaniesServiceInterface, MyFirebaseService() {

    companion object {

        const val COLLECTION_NAME_COMPANIES = "companies"

        const val FIELD_NAME_OWNER_ID = "ownerId"

        const val MESSAGE_DOMAIN_NOT_FOUND = "Could not find the domain "
        const val MESSAGE_DOMAIN_NOT_AVAILABLE = "Domain is not available "

    }

    private val companiesCollection: CollectionReference by lazy {
        firestore.collection(COLLECTION_NAME_COMPANIES)
    }

    override fun isDomainRealAsync(companyDomain: String): Deferred<Boolean> =
        async {
            try {
                val snapshot = companiesCollection.document(companyDomain)
                    .get()
                    .await()
                if (snapshot != null && snapshot.exists()) return@async true
                crashlytics.log(MESSAGE_DOMAIN_NOT_FOUND + companyDomain)
                return@async false
            } catch (exception: FirebaseFirestoreException) {
                crashlytics.recordException(exception)
                return@async false
            }
        }

    override fun isDomainAvailableAsync(companyDomain: String): Deferred<Boolean> =
        async {
            try {
                val snapshot = companiesCollection.document(companyDomain)
                    .get()
                    .await()
                if (snapshot != null && snapshot.exists()) {
                    crashlytics.log(MESSAGE_DOMAIN_NOT_AVAILABLE + companyDomain)
                    return@async false
                }
                return@async true
            } catch (exception: FirebaseFirestoreException) {
                crashlytics.recordException(exception)
                return@async false
            }
        }

    override fun registerDomainAsync(companyDomain: String): Deferred<String> =
        async {
            try {
                val uid = auth.currentUser!!.uid
                val company = Company(id = companyDomain, organizerId = uid)
                companiesCollection.document(companyDomain)
                    .set(company)
                    .await()
                return@async companyDomain
            } catch (exception: FirebaseException) {
                crashlytics.recordException(exception)
                return@async ""
            }
        }

    override fun doDomainBelongToMeAsync(companyDomain: String): Deferred<Boolean> =
        async {
            try {
                val uid = auth.currentUser!!.uid
                val snapshot = companiesCollection.document(companyDomain)
                    .get()
                    .await()
                if (snapshot != null && snapshot[FIELD_NAME_OWNER_ID] == uid)
                    return@async true
                return@async false
            } catch (exception: FirebaseFirestoreException) {
                crashlytics.recordException(exception)
                return@async false
            }
        }

}