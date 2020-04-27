package io.github.horaciocome1.leiloa.ui.product.id

import android.view.View
import androidx.databinding.Bindable
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import io.github.horaciocome1.leiloa.data.company.CompaniesRepository
import io.github.horaciocome1.leiloa.data.product.ProductsRepository
import io.github.horaciocome1.leiloa.util.ObservableViewModel
import io.github.horaciocome1.leiloa.util.navigate
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async

class ProductIdViewModel : ObservableViewModel() {

    private val companiesRepository: CompaniesRepository by lazy {
        CompaniesRepository.getInstance()
    }

    private val productsRepository: ProductsRepository by lazy {
        ProductsRepository.getInstance()
    }

    var companyDomain: String = ""

    @Bindable
    val productId: MutableLiveData<String> = MutableLiveData<String>()

    suspend fun doDomainBelongToMe() = companiesRepository.doDomainBelongToMeAsync(companyDomain).await()


    fun navigateToProductAsync(view: View): Deferred<Boolean> =
        viewModelScope.async {
            val isReal = productsRepository
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