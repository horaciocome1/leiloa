package io.github.horaciocome1.leiloa.ui.company

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

class CompanyDomainViewModel : ObservableViewModel() {

    private val repository: CompaniesRepository by lazy {
        CompaniesRepository.getInstance()
    }

    @Bindable
    val companyDomain: MutableLiveData<String> = MutableLiveData<String>()

    /**
     * checks if the domain is real by checking its existence in the database
     * if it is, it navigates to the next screen
     * if it is not in the database, ir returns false
     */
    fun navigateToProductIdAsync(view: View): Deferred<Boolean> =
        viewModelScope.async {
            val isReal = repository.isDomainRealAsync(companyDomain.value!!)
                .await()
            if (!isReal)
                return@async false
            navigateToProductID(view)
            return@async true
        }

    private fun navigateToProductID(view: View) =
        CompanyDomainFragmentDirections
            .actionCompanyDomainFragmentToProductIdFragment(companyDomain.value!!)
            .navigate(view)

    fun navigateToRegister(view: View) =
        CompanyDomainFragmentDirections
            .actionCompanyDomainFragmentToCompanyDomainRegisterFragment()
            .navigate(view)

}