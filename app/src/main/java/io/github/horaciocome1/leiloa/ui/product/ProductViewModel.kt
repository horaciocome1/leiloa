package io.github.horaciocome1.leiloa.ui.product

import android.view.View
import androidx.lifecycle.viewModelScope
import io.github.horaciocome1.leiloa.data.company.CompaniesRepository
import io.github.horaciocome1.leiloa.data.participants.Participant
import io.github.horaciocome1.leiloa.data.participants.ParticipantsRepository
import io.github.horaciocome1.leiloa.data.participants.ParticipantsService
import io.github.horaciocome1.leiloa.data.product.Product
import io.github.horaciocome1.leiloa.data.product.ProductsRepository
import io.github.horaciocome1.leiloa.util.ObservableViewModel
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class ProductViewModel : ObservableViewModel() {

    private val productsRepository: ProductsRepository by lazy {
        ProductsRepository.getInstance()
    }

    private val participantsRepository: ParticipantsRepository by lazy {
        ParticipantsRepository.getInstance()
    }

    private val companiesRepository: CompaniesRepository by lazy {
        CompaniesRepository.getInstance()
    }

    var companyDomain: String = ""
    var productId: String = ""
    var productActive: Boolean = false

    @ExperimentalCoroutinesApi
    fun watchProduct(): Flow<Product> =
        productsRepository.watchProduct(companyDomain, productId)

    fun doDomainBelongToMeAsync(): Deferred<Boolean> =
        companiesRepository.doDomainBelongToMeAsync(companyDomain)

    fun setActiveStatusAsync(isActive: Boolean = true): Deferred<Boolean> =
        viewModelScope.async {
            if (isActive == productActive)
                return@async false
            return@async productsRepository
                .setActiveStatusAsync(companyDomain, productId, isActive)
                .await()
        }

    @ExperimentalCoroutinesApi
    fun watchParticipants(): Flow<List<Participant>> =
        participantsRepository.watchParticipants(companyDomain, productId)

    fun increase100(view: View, productPrice: Int) {
        view.isEnabled = false
        viewModelScope.launch {
            val price = productPrice + ParticipantsService.INCREASE_100
            participantsRepository.setPriceAsync(companyDomain, productId, price)
                .await()
            view.isEnabled = true
        }
    }

    fun increase500(view: View, productPrice: Int) {
        view.isEnabled = false
        viewModelScope.launch {
            val price = productPrice + ParticipantsService.INCREASE_500
            participantsRepository.setPriceAsync(companyDomain, productId, price)
                .await()
            view.isEnabled = true
        }
    }

    fun increase1000(view: View, productPrice: Int) {
        view.isEnabled = false
        viewModelScope.launch {
            val price = productPrice + ParticipantsService.INCREASE_1000
            participantsRepository.setPriceAsync(companyDomain, productId, price)
                .await()
            view.isEnabled = true
        }
    }

}