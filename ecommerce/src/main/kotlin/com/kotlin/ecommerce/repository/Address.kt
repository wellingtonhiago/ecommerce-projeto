package com.kotlin.ecommerce.repository

data class Address(
    var street: String = "",
    var city: String = "",
    var state: String = "",
    var zipCode: String = ""
) {
}