package com.kotlin.ecommerce.repository

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.math.BigDecimal

@Document(collection = "products")
data class Product(
    @Id
    val productId: ObjectId = ObjectId(),
    val productName: String = "",
    val productPreco: BigDecimal = BigDecimal(0),
    val productDescription: String = "",
    val productType: String = ""

) {
}