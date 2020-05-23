package io.github.horaciocome1.leiloa.ui.company

import android.view.View
import androidx.databinding.Bindable
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import io.github.horaciocome1.leiloa.data.company.CompaniesRepository
import io.github.horaciocome1.leiloa.data.config.RemoteConfigRepository
import io.github.horaciocome1.leiloa.util.ObservableViewModel
import io.github.horaciocome1.leiloa.util.navigate
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async

class CompanyDomainViewModel : ObservableViewModel() {

    @Bindable
    val companyDomain: MutableLiveData<String> = MutableLiveData<String>()

    /**
     * checks if the domain is real by checking its existence in the database
     * if it is, it navigates to the next screen
     * if it is not in the database, ir returns false
     */
    fun navigateToProductIdAsync(view: View): Deferred<Boolean> =
        viewModelScope.async {
            val isReal = CompaniesRepository.isDomainRealAsync(companyDomain.value!!)
                .await()
            if (!isReal)
                return@async false
            navigateToProductID(view)
            return@async true
        }

    fun retrieveCompanyDomainMaxLengthAsync(): Deferred<Long> =
        RemoteConfigRepository.retrieveCompanyDomainMaxLengthAsync()

    fun navigateToRegister(view: View) =
        CompanyDomainFragmentDirections
            .actionOpenCompanyDomainRegister()
            .navigate(view)

    private fun navigateToProductID(view: View) =
        CompanyDomainFragmentDirections
            .actionOpenProductIdFromCompanyDomain(companyDomain.value!!)
            .navigate(view)

}