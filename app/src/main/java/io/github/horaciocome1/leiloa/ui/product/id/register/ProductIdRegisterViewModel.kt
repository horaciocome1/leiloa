package io.github.horaciocome1.leiloa.ui.product.id.register

import android.view.View
import androidx.databinding.Bindable
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import io.github.horaciocome1.leiloa.data.config.RemoteConfigRepository
import io.github.horaciocome1.leiloa.data.product.Product
import io.github.horaciocome1.leiloa.data.product.ProductsRepository
import io.github.horaciocome1.leiloa.util.ObservableViewModel
import io.github.horaciocome1.leiloa.util.navigate
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async

class ProductIdRegisterViewModel : ObservableViewModel() {

    private val productsRepository: ProductsRepository by lazy {
        ProductsRepository.getInstance()
    }

    private val remoteConfigRepository: RemoteConfigRepository by lazy {
        RemoteConfigRepository.getInstance()
    }

    lateinit var companyDomain: String

    @Bindable
    val productId: MutableLiveData<String> = MutableLiveData<String>()

    @Bindable
    val termsAndConditions: MutableLiveData<String> = MutableLiveData<String>()

    @Bindable
    val startOffer: MutableLiveData<String> = MutableLiveData<String>()

    fun retrieveProductIdMaxLengthAsync() =
        remoteConfigRepository.retrieveProductIdMaxLengthAsync()

    fun retrieveTermsAndConditionsMaxLengthAsync() =
        remoteConfigRepository.retrieveTermsAndConditionsMaxLengthAsync()

    fun retrieveStartPriceMaxLengthAsync() =
        remoteConfigRepository.retrieveStartPriceMaxLengthAsync()

    fun retrieveStartActiveAsync() =
        remoteConfigRepository.retrieveStartActiveAsync()

    fun registerProductAsync(view: View, isActive: Boolean): Deferred<Boolean> =
        viewModelScope.async {
            val isSuccessful = productsRepository
                .isProductIdAvailableAsync(companyDomain, productId.value!!)
                .await()
            if (!isSuccessful)
                return@async false
            val id = productsRepository.registerProductAsync(
                companyDomain,
                Product(
                    id = productId.value!!,
                    termsAndConditions = termsAndConditions.value!!,
                    startOffer = startOffer.value!!.toInt(),
                    topOffer = startOffer.value!!.toInt(),
                    active = isActive
                )
            ).await()
            navigateToProduct(view, productId = id)
            return@async true
        }

    private fun navigateToProduct(view: View, productId: String) =
        ProductIdRegisterFragmentDirections
            .actionOpenProductFromProductIdRegister(companyDomain, productId)
            .navigate(view)

}