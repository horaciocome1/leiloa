package io.github.horaciocome1.leiloa.ui.product.id

import android.view.View
import androidx.databinding.Bindable
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import io.github.horaciocome1.leiloa.data.company.CompaniesRepository
import io.github.horaciocome1.leiloa.data.product.ProductsRepository
import io.github.horaciocome1.leiloa.util.ObservableViewModel
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

    fun doDomainBelongToMe() = companiesRepository.doDomainBelongToMeAsync(companyDomain)


    fun navigateToProductAsync(view: View): Deferred<Boolean> =
        viewModelScope.async {
            val isReal = productsRepository.isProductIdRealAsync(companyDomain, productId.value!!)
                .await()
            if (!isReal)
                return@async false
            navigateToProduct(view)
            return@async true
        }

    private fun navigateToProduct(view: View) {
        val directions = ProductIdFragmentDirections
            .actionProductIdFragmentToProductFragment(companyDomain, productId.value!!)
        view.findNavController()
            .navigate(directions)
    }

    fun navigateToProductIdRegister(view: View) {
        val directions = ProductIdFragmentDirections
            .actionProductIdFragmentToRegisterProductIdFragment(companyDomain)
        view.findNavController()
            .navigate(directions)
    }

}