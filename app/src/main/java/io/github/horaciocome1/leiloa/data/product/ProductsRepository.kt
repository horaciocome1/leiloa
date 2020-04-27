package io.github.horaciocome1.leiloa.data.product

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow

class ProductsRepository private constructor(
    private val service: ProductsService = ProductsService()
) : ProductsServiceInterface {

    companion object {

        @Volatile
        private var instance: ProductsRepository? = null

        fun getInstance() = instance ?: synchronized(this) {
            instance ?: ProductsRepository().also { instance = it }
        }

    }

    override fun isProductIdRealAsync(
        companyDomain: String,
        productID: String
    ): Deferred<Boolean> =
        service.isProductIdRealAsync(companyDomain, productID)

    override fun isProductIdAvailableAsync(
        companyDomain: String,
        productID: String
    ): Deferred<Boolean> =
        service.isProductIdAvailableAsync(companyDomain, productID)

    override fun registerProductAsync(
        companyDomain: String,
        product: Product
    ): Deferred<String> =
        service.registerProductAsync(companyDomain, product)

    override fun setActiveStatusAsync(
        companyDomain: String,
        productID: String,
        isActive: Boolean
    ): Deferred<Boolean> =
        service.setActiveStatusAsync(companyDomain, productID, isActive)

    @ExperimentalCoroutinesApi
    override fun watchProduct(
        companyDomain: String,
        productID: String
    ): Flow<Product> =
        service.watchProduct(companyDomain, productID)
}