package io.github.horaciocome1.leiloa.data.company

import com.google.firebase.Timestamp

data class Company(
    var id: String = "",
    var ownerId: String = "",
    var createdAt: Timestamp = Timestamp.now()
)