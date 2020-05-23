package io.github.horaciocome1.leiloa.ui.product

import android.net.Uri
import android.view.View
import androidx.lifecycle.viewModelScope
import com.google.firebase.dynamiclinks.ktx.*
import com.google.firebase.ktx.Firebase
import io.github.horaciocome1.leiloa.BuildConfig
import io.github.horaciocome1.leiloa.data.company.CompaniesRepository
import io.github.horaciocome1.leiloa.data.participants.Participant
import io.github.horaciocome1.leiloa.data.participants.ParticipantsRepository
import io.github.horaciocome1.leiloa.data.participants.ParticipantsService
import io.github.horaciocome1.leiloa.data.product.Product
import io.github.horaciocome1.leiloa.data.product.ProductsRepository
import io.github.horaciocome1.leiloa.util.ObservableViewModel
import io.github.horaciocome1.leiloa.util.myCrashlytics
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ProductViewModel : ObservableViewModel() {

    lateinit var companyDomain: String

    lateinit var productId: String

    var productActive: Boolean = false

    @ExperimentalCoroutinesApi
    fun watchProduct(): Flow<Product> =
        ProductsRepository.watchProduct(companyDomain, productId)

    fun doDomainBelongToMeAsync(): Deferred<Boolean> =
        CompaniesRepository.doDomainBelongToMeAsync(companyDomain)

    fun setActiveStatusAsync(isActive: Boolean = true): Deferred<Boolean> =
        viewModelScope.async {
            if (isActive == productActive)
                return@async false
            return@async ProductsRepository
                .setActiveStatusAsync(companyDomain, productId, isActive)
                .await()
        }

    @ExperimentalCoroutinesApi
    fun watchParticipants(): Flow<List<Participant>> =
        ParticipantsRepository.watchParticipants(companyDomain, productId)

    fun retrieveDynamicLinkAsync(fallbackUrl: Uri) = viewModelScope.async {
        try {
            val shortLink = Firebase.dynamicLinks.shortLinkAsync {
                val url = "https://leiloa.web.app/?d=$companyDomain&p=$productId"
                link = Uri.parse(url)
                domainUriPrefix = "https://leiloa.page.link"
                androidParameters(BuildConfig.APPLICATION_ID) {
                    this.fallbackUrl = fallbackUrl
                    minimumVersion = 21
                }
                socialMetaTagParameters {
                    title = companyDomain
                    description = productId
                }
            }.await()
            return@async shortLink.previewLink
        } catch (exception: Exception) {
            Firebase.myCrashlytics.recordException(exception)
            return@async null
        }
    }

    fun increase100(view: View, topOffer: Int) {
        view.isEnabled = false
        val offer = topOffer + ParticipantsService.INCREASE_100
        viewModelScope.launch {
            ParticipantsRepository.setPriceAsync(companyDomain, productId, offer)
                .await()
            view.isEnabled = true
        }
    }

    fun increase500(view: View, topOffer: Int) {
        view.isEnabled = false
        val offer = topOffer + ParticipantsService.INCREASE_500
        viewModelScope.launch {
            ParticipantsRepository.setPriceAsync(companyDomain, productId, offer)
                .await()
            view.isEnabled = true
        }
    }

    fun increase1000(view: View, topOffer: Int) {
        view.isEnabled = false
        val offer = topOffer + ParticipantsService.INCREASE_1000
        viewModelScope.launch {
            ParticipantsRepository.setPriceAsync(companyDomain, productId, offer)
                .await()
            view.isEnabled = true
        }
    }

}