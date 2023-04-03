package com.kotlin.ecommerce

import com.kotlin.ecommerce.repository.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal

@RestController
@RequestMapping("/ecommerce")
class Controller(
    @Autowired
    private val users : UserRepository,
    @Autowired
    private val products: ProductRepository
) {

    //Product
    @GetMapping("/number-of-products")
    fun getCountProducts(): Int = products.findAll().count()

    @GetMapping("/product/{id}")
    fun getProductById(@PathVariable("id") id: String): ResponseEntity<Product> =
        if (products.findByProductId(id) != null) ResponseEntity.ok(products.findByProductId(id))
        else ResponseEntity.notFound().build()

    @PostMapping("/add-product")
    fun postProduct(@RequestBody product: Product) = ResponseEntity.ok(products.save(product))

    @PatchMapping("update-product/{id}")
    fun updateProduct(@PathVariable id: String, @RequestBody updates: Map<String, Any>): ResponseEntity<Product> {
        val product = products.findByProductId(id) ?: return ResponseEntity.notFound().build()

        val updatedProduct = updateProductFields(product, updates)
        products.save(updatedProduct)

        return ResponseEntity.ok(updatedProduct)
    }

    @DeleteMapping("/delete-product/{id}")
    fun deleteProduct(@PathVariable("id") id: String) {
        products.findByProductId(id)?.let {
            products.delete(it)
        }
    }


    //User
    @GetMapping("/number-of-users")
    fun getCountUsers(): Int = users.findAll().count()

    @GetMapping("/user/{id}")
    fun getUserById(@PathVariable("id") id: String): ResponseEntity<User> =
        if (users.findByUserId(id) != null) ResponseEntity.ok(users.findByUserId(id))
        else ResponseEntity.notFound().build()

    @PostMapping("/add-user")
    fun postUser(@RequestBody user: User) = ResponseEntity.ok(users.save(user))

    @PatchMapping("/update-user/{id}")
    fun updateUser(@PathVariable id: String, @RequestBody updates: Map<String, Any>): ResponseEntity<User> {
        val user = users.findByUserId(id) ?: return ResponseEntity.notFound().build()

        val updatedUser = updateUserFields(user, updates)
        users.save(updatedUser)

        return ResponseEntity.ok(updatedUser)
    }

    @DeleteMapping("/delete-user/{id}")
    fun deleteUser(@PathVariable("id") id: String) {
        users.findByUserId(id)?.let {
            users.delete(it)
        }
    }

}

// funções para atualização de user e product
private fun updateUserFields(user: User, updates: Map<String, Any>): User {
    var updatedUser = user.copy()

    for ((key, value) in updates) {
        when (key) {
            "userName" -> updatedUser = updatedUser.copy(userName = value as String)
            "password" -> updatedUser = updatedUser.copy(password = value as String)
            "email" -> updatedUser = updatedUser.copy(email = value as String)
            "address" -> {
                val addressMap = value as Map<*, *>
                val address = Address(
                    street = addressMap["street"] as String? ?: user.address?.street ?: "",
                    city = addressMap["city"] as String? ?: user.address?.city ?: "",
                    state = addressMap["state"] as String? ?: user.address?.state ?: "",
                    zipCode = addressMap["zipCode"] as String? ?: user.address?.zipCode ?: ""
                )
                updatedUser = updatedUser.copy(address = address)
            }
        }
    }

    return updatedUser
}

private fun updateProductFields(product: Product, updates: Map<String, Any>): Product {
    var updatedProduct = product.copy()

    for ((key, value) in updates) {
        when (key) {
            "productName" -> updatedProduct = updatedProduct.copy(productName = value as String)
            "productPreco" -> updatedProduct = updatedProduct.copy(productPreco = value as BigDecimal)
            "productDescription" -> updatedProduct = updatedProduct.copy(productDescription = value as String)
            "productType" -> updatedProduct = updatedProduct.copy(productType = value as String)
        }
    }

    return updatedProduct
}