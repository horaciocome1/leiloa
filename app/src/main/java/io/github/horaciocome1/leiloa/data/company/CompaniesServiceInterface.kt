package io.github.horaciocome1.leiloa.data.company

import kotlinx.coroutines.Deferred

interface CompaniesServiceInterface {

    /**
     * tells if informed domain exists or not in the database
     */
    fun isDomainRealAsync(companyDomain: String): Deferred<Boolean>

    /**
     * tells if informed domain is available for registering in the database
     */
    fun isDomainAvailableAsync(companyDomain: String): Deferred<Boolean>

    /**
     * registers the informed domain to the database
     */
    fun registerDomainAsync(companyDomain: String): Deferred<String>

    /**
     * tells whether the informed domain belong to the logged user or not
     */
    fun doDomainBelongToMeAsync(companyDomain: String): Deferred<Boolean>

}