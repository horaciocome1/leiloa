package io.github.horaciocome1.leiloa.ui.company.register

import android.view.View
import androidx.databinding.Bindable
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import io.github.horaciocome1.leiloa.data.company.CompaniesRepository
import io.github.horaciocome1.leiloa.util.ObservableViewModel
import io.github.horaciocome1.leiloa.util.navigate
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async

class CompanyDomainRegisterViewModel : ObservableViewModel() {

    private val repository: CompaniesRepository by lazy {
        CompaniesRepository.getInstance()
    }

    @Bindable
    val companyDomain: MutableLiveData<String> = MutableLiveData<String>()

    /**
     * checks the database to see if the domain is available
     * if the domain is available it registers the domain and returns true
     * if the domain is not available it just return false
     */
    fun registerDomainAsync(view: View): Deferred<Boolean> =
        viewModelScope.async {
            val isAvailable = repository.isDomainAvailableAsync(companyDomain.value!!)
                .await()
            if (!isAvailable)
                return@async false
            val domain = repository.registerDomainAsync(companyDomain.value!!)
                .await()
            navigateToProductID(view, domain)
            return@async true
        }

    private fun navigateToProductID(view: View, domain: String) =
        CompanyDomainRegisterFragmentDirections
            .actionCompanyDomainRegisterFragmentToProductIdFragment(domain)
            .navigate(view)

}