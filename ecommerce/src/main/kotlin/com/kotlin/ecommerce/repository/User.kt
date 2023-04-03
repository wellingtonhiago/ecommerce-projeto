package com.kotlin.ecommerce.repository

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "users")
data class User(
    @Id
    val userId: ObjectId = ObjectId(),
    val userName: String = "",
    val password: String = "",
    val email: String = "",
    val address: Address? = Address()
) {
}