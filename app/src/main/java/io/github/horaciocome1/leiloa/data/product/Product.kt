package io.github.horaciocome1.leiloa.data.product

import com.google.firebase.Timestamp


data class Product(
    var id: String = "",
    var termsAndConditions: String = "",
    var price: Int = 0,
    var startPrice: Int = 0,
    var active: Boolean = false,
    var createdAt: Timestamp = Timestamp.now()
)