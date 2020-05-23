package io.github.horaciocome1.leiloa.data.company

import kotlinx.coroutines.Deferred

object CompaniesRepository: CompaniesServiceInterface {

    private val service: CompaniesService = CompaniesService()

    override fun isDomainRealAsync(companyDomain: String): Deferred<Boolean> =
        service.isDomainRealAsync(companyDomain)

    override fun isDomainAvailableAsync(companyDomain: String): Deferred<Boolean> =
        service.isDomainAvailableAsync(companyDomain)

    override fun registerDomainAsync(companyDomain: String): Deferred<String> =
        service.registerDomainAsync(companyDomain)

    override fun doDomainBelongToMeAsync(companyDomain: String): Deferred<Boolean> =
        service.doDomainBelongToMeAsync(companyDomain)

}