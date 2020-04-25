package io.github.horaciocome1.leiloa.data.company

import kotlinx.coroutines.Deferred

class CompaniesRepository private constructor(
    private val service: CompaniesService = CompaniesService()
) : CompaniesServiceInterface {

    companion object {

        @Volatile
        private var instance: CompaniesRepository? = null

        fun getInstance() = instance ?: synchronized(this) {
            instance ?: CompaniesRepository().also { instance = it }
        }

    }

    override fun isDomainRealAsync(companyDomain: String): Deferred<Boolean> =
        service.isDomainRealAsync(companyDomain)

    override fun isDomainAvailableAsync(companyDomain: String): Deferred<Boolean> =
        service.isDomainAvailableAsync(companyDomain)

    override fun registerDomainAsync(companyDomain: String): Deferred<String> =
        service.registerDomainAsync(companyDomain)

    override fun doDomainBelongToMeAsync(companyDomain: String): Deferred<Boolean> =
        service.doDomainBelongToMeAsync(companyDomain)

}