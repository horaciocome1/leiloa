package io.github.horaciocome1.leiloa.data.participants

data class Participant(
    var id: String = "",
    var name: String = "",
    var offer: Int = 0
) {

    /*
    introduced in release v0.0.4
    remove on 05/08/2020, when there is nobody using v0.0.3 or lower
    it stays just for backwards compatibility
     */

    val price: Int
        get() = offer

}