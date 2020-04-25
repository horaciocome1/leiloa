package io.github.horaciocome1.leiloa.ui.product.id.register

import android.view.View
import androidx.databinding.Bindable
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import io.github.horaciocome1.leiloa.data.product.Product
import io.github.horaciocome1.leiloa.data.product.ProductsRepository
import io.github.horaciocome1.leiloa.util.ObservableViewModel
import io.github.horaciocome1.leiloa.util.navigate
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async

class ProductIdRegisterViewModel : ObservableViewModel() {

    private val repository: ProductsRepository by lazy {
        ProductsRepository.getInstance()
    }

    lateinit var companyDomain: String

    @Bindable
    val productId: MutableLiveData<String> = MutableLiveData<String>()

    @Bindable
    val termsAndConditions: MutableLiveData<String> = MutableLiveData<String>()

    @Bindable
    val startPrice: MutableLiveData<String> = MutableLiveData<String>()

    fun registerProductAsync(view: View, isActive: Boolean): Deferred<Boolean> =
        viewModelScope.async {
            val isSuccessful = repository.isProductIdAvailableAsync(companyDomain, productId.value!!)
                .await()
            if (!isSuccessful)
                return@async false
            val id = repository.registerProductAsync(
                companyDomain,
                Product(
                    id = productId.value!!,
                    termsAndConditions = termsAndConditions.value!!,
                    price = startPrice.value!!.toInt(),
                    active = isActive
                )
            ).await()
            navigateToProduct(view, productId = id)
            return@async true
        }

    private fun navigateToProduct(view: View, productId: String) =
        ProductIdRegisterFragmentDirections
            .actionRegisterProductIdFragmentToProductFragment(companyDomain, productId)
            .navigate(view)

}