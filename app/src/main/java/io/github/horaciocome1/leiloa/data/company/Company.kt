package io.github.horaciocome1.leiloa.data.company

import com.google.firebase.Timestamp

data class Company(
    var id: String = "",
    var organizerId: String = "",
    var createdAt: Timestamp = Timestamp.now()
) {

    /*
    introduced in release v0.0.4
    remove on 05/08/2020, when there is nobody using v0.0.3 or lower
    it stays just for backwards compatibility
     */

    val ownerId: String
        get() = organizerId

}