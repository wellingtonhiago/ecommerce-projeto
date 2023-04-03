package com.kotlin.ecommerce.repository

import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface ProductRepository : MongoRepository<Product, String> {

    fun findByProductId(id : String): Product?
}