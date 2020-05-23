package io.github.horaciocome1.leiloa.ui.product.id

import android.view.View
import androidx.databinding.Bindable
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import io.github.horaciocome1.leiloa.data.company.CompaniesRepository
import io.github.horaciocome1.leiloa.data.config.RemoteConfigRepository
import io.github.horaciocome1.leiloa.data.product.ProductsRepository
import io.github.horaciocome1.leiloa.util.ObservableViewModel
import io.github.horaciocome1.leiloa.util.navigate
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async

class ProductIdViewModel: ObservableViewModel() {

    lateinit var companyDomain: String

    @Bindable
    val productId: MutableLiveData<String> = MutableLiveData<String>()

    fun doDomainBelongToMeAsync() =
        CompaniesRepository.doDomainBelongToMeAsync(companyDomain)

    fun retrieveProductIdMaxLengthAsync() =
        RemoteConfigRepository.retrieveProductIdMaxLengthAsync()

    fun navigateToProductAsync(view: View): Deferred<Boolean> =
        viewModelScope.async {
            val isReal = ProductsRepository
                .isProductIdRealAsync(companyDomain, productId.value!!)
                .await()
            if (!isReal)
                return@async false
            navigateToProduct(view)
            return@async true
        }

    private fun navigateToProduct(view: View) =
        ProductIdFragmentDirections
            .actionOpenProductFromProductId(companyDomain, productId.value!!)
            .navigate(view)

    fun navigateToProductIdRegister(view: View) =
        ProductIdFragmentDirections
            .actionOpenProductIdRegister(companyDomain)
            .navigate(view)

}