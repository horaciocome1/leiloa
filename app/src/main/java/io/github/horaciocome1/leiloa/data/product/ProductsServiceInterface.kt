package io.github.horaciocome1.leiloa.data.product

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow

interface ProductsServiceInterface {

    /**
     * tells whether the product exists in the database or not
     */
    fun isProductIdRealAsync(
        companyDomain: String,
        productID: String
    ): Deferred<Boolean>

    /**
     * tells whether the product id is available in the database or not
     */
    fun isProductIdAvailableAsync(
        companyDomain: String,
        productID: String
    ): Deferred<Boolean>

    /**
     * registers the product to the database
     */
    fun registerProductAsync(
        companyDomain: String,
        product: Product
    ): Deferred<String>

    /**
     * changes the active status of the product
     */
    fun setActiveStatusAsync(
        companyDomain: String,
        productID: String,
        isActive: Boolean = true
    ): Deferred<Boolean>

    /**
     * returns a kotlin flow of products
     * each time there is a difference in database, it send new product through the flow
     */
    @ExperimentalCoroutinesApi
    fun watchProduct(
        companyDomain: String,
        productID: String
    ): Flow<Product>

}