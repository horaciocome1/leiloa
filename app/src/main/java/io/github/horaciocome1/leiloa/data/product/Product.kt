package io.github.horaciocome1.leiloa.data.product

import com.google.firebase.Timestamp


data class Product(
    var id: String = "",
    var termsAndConditions: String = "",
    var topOffer: Int = 0,
    var startOffer: Int = 0,
    var active: Boolean = false,
    var createdAt: Timestamp = Timestamp.now()
) {

    /*
    introduced in release v0.0.4
    remove on 05/08/2020, when there is nobody using v0.0.3 or lower
    it stays just for backwards compatibility
     */

    val price: Int
        get() = topOffer

    val startPrice: Int
        get() = startOffer

}